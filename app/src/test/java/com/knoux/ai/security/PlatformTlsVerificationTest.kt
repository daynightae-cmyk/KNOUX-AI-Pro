package com.knoux.ai.security

import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLException
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

/**
 * Proves the app relies on genuine platform certificate-chain and hostname
 * verification, using real TLS handshakes against a local HTTPS server.
 *
 * These tests intentionally use the JDK/Android default SSLSocketFactory and default
 * HostnameVerifier. If ProductionSecurity ever reinstalls a permissive global trust
 * manager, the "untrusted certificate" test starts passing a connection that must be
 * rejected, and this suite fails.
 */
class PlatformTlsVerificationTest {

    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
    }

    @After
    fun tearDown() {
        runCatching { server.shutdown() }
    }

    private fun certificateFor(vararg sans: String): HeldCertificate {
        val builder = HeldCertificate.Builder()
            .commonName(sans.firstOrNull() ?: "localhost")
            .validityInterval(
                System.currentTimeMillis() - 60_000,
                System.currentTimeMillis() + 3_600_000,
            )
        sans.forEach { builder.addSubjectAlternativeName(it) }
        return builder.build()
    }

    private fun startServer(certificate: HeldCertificate) {
        val serverCertificates = HandshakeCertificates.Builder()
            .heldCertificate(certificate)
            .build()
        server.useHttps(serverCertificates.sslSocketFactory(), false)
        server.enqueue(MockResponse().setBody("ok"))
        server.start()
    }

    private fun connect(
        url: String,
        trust: HandshakeCertificates? = null,
        usePlatformDefaults: Boolean = true,
    ): Int {
        val connection = URL(url).openConnection() as HttpsURLConnection
        connection.connectTimeout = 8_000
        connection.readTimeout = 8_000
        if (!usePlatformDefaults && trust != null) {
            connection.sslSocketFactory = trust.sslSocketFactory()
        }
        return try {
            connection.responseCode
        } finally {
            connection.disconnect()
        }
    }

    @Test
    fun `untrusted self signed certificate is rejected by platform defaults`() {
        val certificate = certificateFor("localhost")
        startServer(certificate)
        try {
            connect(server.url("/").toString())
            fail("A self-signed certificate must never be accepted by platform defaults")
        } catch (expected: SSLException) {
            assertTrue(expected.message?.isNotBlank() == true)
        }
    }

    @Test
    fun `trusted certificate with matching hostname succeeds`() {
        // MockWebServer advertises its loopback IP, so the certificate carries both
        // a DNS and an IP subject alternative name.
        val certificate = certificateFor("localhost", "127.0.0.1")
        startServer(certificate)
        val trust = HandshakeCertificates.Builder()
            .addTrustedCertificate(certificate.certificate)
            .build()
        val code = connect(server.url("/").toString(), trust = trust, usePlatformDefaults = false)
        assertEquals(200, code)
    }

    @Test
    fun `trusted certificate for the wrong hostname is rejected`() {
        val certificate = certificateFor("wrong.example")
        startServer(certificate)
        val trust = HandshakeCertificates.Builder()
            .addTrustedCertificate(certificate.certificate)
            .build()
        try {
            connect(server.url("/").toString(), trust = trust, usePlatformDefaults = false)
            fail("Hostname verification must reject a certificate issued for another host")
        } catch (expected: SSLException) {
            assertTrue(expected.message?.isNotBlank() == true)
        }
    }
}
