package com.knoux.ai

import androidx.sqlite.db.SupportSQLiteDatabase
import com.knoux.ai.database.AppDatabase
import java.io.File
import java.lang.reflect.Proxy
import org.junit.Assert.assertEquals
import org.junit.Test

/** Captures actual migration SQL on the JVM; does not execute a device migration. */
class AppDatabaseMigrationJvmTest {
    @Test
    fun migrationSqlMatchesExportedV2BatteryTableExactly() {
        val statements = mutableListOf<String>()
        val database = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java),
        ) { _, method, arguments ->
            check(method.name == "execSQL") { "Unexpected database call: ${method.name}" }
            statements += arguments!![0] as String
            null
        } as SupportSQLiteDatabase

        val migration = AppDatabase.MIGRATION_1_2
        assertEquals(1, migration.startVersion)
        assertEquals(2, migration.endVersion)
        migration.migrate(database)

        val schema = appContractFile("schemas/com.knoux.ai.database.AppDatabase/2.json").readText()
        val exportedSql = Regex(
            "\"tableName\":\\s*\"battery_samples\",\\s*\"createSql\":\\s*\"([^\"]+)\"",
        ).find(schema)!!.groupValues[1].replace("\${TABLE_NAME}", "battery_samples")

        assertEquals(1, statements.size)
        fun normalize(sql: String) = sql.replace(Regex("\\s+"), "")
        assertEquals(normalize(exportedSql), normalize(statements.single()))
    }
}

/** Gradle may run JVM tests from either the root project or the app project. */
internal fun appContractFile(relativePath: String): File {
    val workingDirectory = File(System.getProperty("user.dir")).absoluteFile
    return listOf(workingDirectory, workingDirectory.parentFile)
        .map { File(it, "app/$relativePath").absoluteFile }
        .firstOrNull { it.isFile }
        ?: error("Cannot locate app contract: $relativePath from $workingDirectory")
}
