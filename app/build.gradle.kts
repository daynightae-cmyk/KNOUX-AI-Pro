plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("androidx.room") version "2.7.2"
    id("com.google.devtools.ksp")
}

// OAuth client IDs are public identifiers, never secrets. Supply the real
// values at build time (gradle.properties / -P) so no placeholder is shipped.
// GOOGLE_ANDROID_CLIENT_ID is the installed-app client used for the
// authorization-code + PKCE flow; GOOGLE_WEB_CLIENT_ID is only for ID tokens.
val googleAndroidClientId = (project.findProperty("knoux.googleAndroidClientId") as String?)
    ?.trim().orEmpty()
val microsoftClientId = (project.findProperty("knoux.microsoftClientId") as String?)
    ?.trim().orEmpty()
val googleOAuthScheme = if (googleAndroidClientId.isNotBlank()) {
    "com.googleusercontent.apps.${googleAndroidClientId.removeSuffix(".apps.googleusercontent.com")}"
} else {
    "knoux.oauth.unconfigured"
}

android {
    namespace = "com.knoux.ai"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.knoux.ai"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += listOf("en", "ar")

        buildConfigField("String", "SUPABASE_URL", "\"https://tduofkizwkjtqcbklfah.supabase.co\"")
        buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", "\"sb_publishable_19JCfWlBOdUng5LHAoMfRg_hlFl7YnC\"")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"966864797292-6gqjaebu6n3il0777g3jf0q0mc22jr29.apps.googleusercontent.com\"")
        buildConfigField("String", "GOOGLE_ANDROID_CLIENT_ID", "\"$googleAndroidClientId\"")
        buildConfigField("String", "MICROSOFT_CLIENT_ID", "\"$microsoftClientId\"")
        manifestPlaceholders["googleOAuthScheme"] = googleOAuthScheme
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        // The build that updates the launcher app the owner actually opens
        // (com.knoux.ai, no ".debug" suffix). Debug-signed so it installs in
        // place over the existing preview without touching user data.
        create("preview") {
            initWith(getByName("debug"))
            applicationIdSuffix = ""
            versionNameSuffix = "-preview"
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true; buildConfig = true }
    packaging {
        resources { excludes += setOf("/META-INF/{AL2.0,LGPL2.1}") }
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.01.00"))
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.credentials:credentials:1.6.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    // Room — local audit/history journal for service operations
    val roomVersion = "2.7.2"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
    // Real org.json on the JVM test classpath: android.jar's stubbed JSONObject
    // throws "not mocked". This lets auth/session contract tests execute the
    // production JSON parsing code instead of a reimplementation.
    testImplementation("org.json:json:20240303")
    // Test-only TLS fixtures: real certificate chains for hostname/trust tests.
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("com.squareup.okhttp3:okhttp-tls:4.12.0")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}
