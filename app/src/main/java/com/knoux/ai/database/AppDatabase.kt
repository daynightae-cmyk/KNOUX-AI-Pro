package com.knoux.ai.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

// ============================================================================
// ENTITIES
// ============================================================================

@Entity(tableName = "scan_sessions")
data class ScanSessionEntity(
    @PrimaryKey val id: String,
    val serviceId: String,
    val serviceName: String,
    val startedAt: Long,
    val endedAt: Long? = null,
    val status: String,
    val permissionGranted: Boolean = false,
    val itemsFound: Int = 0,
    val bytesFound: Long = 0,
    val bytesReclaimable: Long = 0,
    val deviceModel: String? = null,
    val apiLevel: Int? = null,
)

@Entity(
    tableName = "observed_items",
    primaryKeys = ["scanSessionId", "providerId"],
    foreignKeys = [
        ForeignKey(
            entity = ScanSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["scanSessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("scanSessionId")]
)
data class ObservedItemEntity(
    val scanSessionId: String,
    val providerId: String,
    val uri: String? = null,
    val mediaStoreId: Long? = null,
    val name: String? = null,
    val sizeBytes: Long? = null,
    val modifiedAt: Long? = null,
    val scope: String? = null,
    val bucket: String? = null,
    val isVideo: Boolean = false,
    val isDuplicate: Boolean = false,
    val duplicateGroupId: String? = null,
    val isKeeper: Boolean = false,
    val lastSeen: Long? = null,
)

@Entity(
    tableName = "operation_requests",
    foreignKeys = [
        ForeignKey(
            entity = ScanSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["scanSessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("scanSessionId"), Index("status")]
)
data class OperationRequestEntity(
    @PrimaryKey val id: String,
    val scanSessionId: String,
    val serviceId: String,
    val requestedAction: String,
    val selectionCount: Int = 0,
    val keeperIds: String? = null,
    val exclusionIds: String? = null,
    val predictedEligibleBytes: Long? = null,
    val consentGiven: Boolean = false,
    val consentAt: Long? = null,
    val createdAt: Long,
    val status: String = "PENDING",
)

@Entity(
    tableName = "operation_item_outcomes",
    primaryKeys = ["operationId", "itemId"],
    foreignKeys = [
        ForeignKey(
            entity = OperationRequestEntity::class,
            parentColumns = ["id"],
            childColumns = ["operationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("operationId")]
)
data class OperationItemOutcomeEntity(
    val operationId: String,
    val itemId: String,
    val result: String,
    val postQueryExists: Boolean = false,
    val measuredBytes: Long? = null,
    val providerError: String? = null,
    val completedAt: Long? = null,
)

@Entity(tableName = "scoped_grants")
data class ScopedGrantEntity(
    @PrimaryKey val id: String,
    val uri: String,
    val flags: Int = 0,
    val grantedAt: Long,
    val persistable: Boolean = false,
    val lastValidated: Long? = null,
    val valid: Boolean = true,
)

@Entity(tableName = "storage_snapshots")
data class StorageSnapshotEntity(
    @PrimaryKey val id: String,
    val totalBytes: Long = 0L,
    val usedBytes: Long = 0L,
    val freeBytes: Long = 0L,
    val mediaBytes: Long = 0L,
    val cacheBytes: Long = 0L,
    val otherBytes: Long = 0L,
    val recordedAt: Long,
    val scope: String = "device",
)

@Entity(tableName = "battery_samples")
data class BatterySampleEntity(
    @PrimaryKey val id: String,
    val levelPercent: Int?,
    val status: String?,
    val temperatureC: Float?,
    val voltageMv: Int?,
    val technology: String?,
    val charging: Boolean = false,
    val health: String?,
    val recordedAt: Long,
)

@Entity(tableName = "permission_events")
data class PermissionEventEntity(
    @PrimaryKey val id: String,
    val permission: String,
    val granted: Boolean = false,
    val eventAt: Long,
    val source: String = "user",
)

@Entity(tableName = "feature_capabilities")
data class FeatureCapabilityEntity(
    @PrimaryKey val featureId: String,
    val serviceId: String,
    val capabilityStatus: String,
    val apiSource: String? = null,
    val permissionRequired: String? = null,
    val lastChecked: Long,
    val reason: String? = null,
)

/** Aggregated, measured outcome of one operation - never an estimate. */
data class OperationOutcomeSummary(
    val operationId: String,
    val itemCount: Int,
    val measuredBytes: Long,
    val successCount: Int,
    val noChangeCount: Int,
)

// ============================================================================
// DAOs
// ============================================================================

@Dao
interface ScanSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: ScanSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<ScanSessionEntity>)

    @Update
    suspend fun update(session: ScanSessionEntity)

    @Query("SELECT * FROM scan_sessions WHERE id = :id")
    suspend fun getById(id: String): ScanSessionEntity?

    @Query("SELECT * FROM scan_sessions WHERE serviceId = :serviceId ORDER BY startedAt DESC")
    fun getByService(serviceId: String): Flow<List<ScanSessionEntity>>

    @Query("SELECT * FROM scan_sessions ORDER BY startedAt DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<ScanSessionEntity>>

    @Query("DELETE FROM scan_sessions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM scan_sessions WHERE serviceId = :serviceId")
    suspend fun countByService(serviceId: String): Int
}

@Dao
interface ObservedItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ObservedItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ObservedItemEntity>)

    @Query("SELECT * FROM observed_items WHERE scanSessionId = :scanSessionId")
    fun getBySession(scanSessionId: String): Flow<List<ObservedItemEntity>>

    @Query("SELECT * FROM observed_items WHERE scanSessionId = :scanSessionId AND isDuplicate = 1")
    fun getDuplicatesBySession(scanSessionId: String): Flow<List<ObservedItemEntity>>

    @Query("DELETE FROM observed_items WHERE scanSessionId = :scanSessionId")
    suspend fun deleteBySession(scanSessionId: String)

    @Query("SELECT COUNT(*) FROM observed_items WHERE scanSessionId = :scanSessionId")
    suspend fun countBySession(scanSessionId: String): Int
}

@Dao
interface OperationRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: OperationRequestEntity)

    // Never REPLACE a parent: that could cascade-delete its existing journal rows.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLegacyParent(session: ScanSessionEntity)

    @Transaction
    suspend fun insertWithLegacyParent(request: OperationRequestEntity) {
        val resolvedRequest = if (request.scanSessionId.isEmpty()) {
            val parentId = java.util.UUID.randomUUID().toString()
            insertLegacyParent(
                ScanSessionEntity(
                    id = parentId,
                    serviceId = request.serviceId,
                    serviceName = request.serviceId,
                    startedAt = request.createdAt,
                    endedAt = request.createdAt,
                    // Journal provenance only; this does not claim a scan or permission.
                    status = "LEGACY_OPERATION",
                )
            )
            request.copy(scanSessionId = parentId)
        } else {
            request
        }
        insert(resolvedRequest)
    }

    @Update
    suspend fun update(request: OperationRequestEntity)

    @Query("SELECT * FROM operation_requests WHERE id = :id")
    suspend fun getById(id: String): OperationRequestEntity?

    @Query("SELECT * FROM operation_requests WHERE scanSessionId = :scanSessionId ORDER BY createdAt DESC")
    fun getBySession(scanSessionId: String): Flow<List<OperationRequestEntity>>

    @Query("SELECT * FROM operation_requests WHERE status = :status ORDER BY createdAt DESC")
    fun getByStatus(status: String): Flow<List<OperationRequestEntity>>

    @Query("SELECT * FROM operation_requests ORDER BY createdAt DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<OperationRequestEntity>>

    @Query("SELECT * FROM operation_requests ORDER BY createdAt DESC")
    fun getAll(): Flow<List<OperationRequestEntity>>

    @Query("UPDATE operation_requests SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("SELECT COUNT(*) FROM operation_requests WHERE serviceId = :serviceId")
    suspend fun countByService(serviceId: String): Int
}

@Dao
interface OperationItemOutcomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(outcome: OperationItemOutcomeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(outcomes: List<OperationItemOutcomeEntity>)

    @Query("SELECT * FROM operation_item_outcomes WHERE operationId = :operationId")
    fun getByOperation(operationId: String): Flow<List<OperationItemOutcomeEntity>>

    @Query("SELECT * FROM operation_item_outcomes WHERE operationId = :operationId AND result = :result")
    fun getByOperationAndResult(operationId: String, result: String): Flow<List<OperationItemOutcomeEntity>>

    @Query(
        """
        SELECT operationId AS operationId,
               COUNT(*) AS itemCount,
               SUM(COALESCE(measuredBytes, 0)) AS measuredBytes,
               SUM(CASE WHEN result = 'SUCCESS' THEN 1 ELSE 0 END) AS successCount,
               SUM(CASE WHEN result = 'NO_CHANGE' THEN 1 ELSE 0 END) AS noChangeCount
        FROM operation_item_outcomes
        GROUP BY operationId
        """
    )
    fun summaries(): Flow<List<OperationOutcomeSummary>>
}

@Dao
interface ScopedGrantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(grant: ScopedGrantEntity)

    @Update
    suspend fun update(grant: ScopedGrantEntity)

    @Query("SELECT * FROM scoped_grants WHERE uri = :uri")
    suspend fun getByUri(uri: String): ScopedGrantEntity?

    @Query("SELECT * FROM scoped_grants WHERE valid = 1")
    fun getAllValid(): Flow<List<ScopedGrantEntity>>

    @Query("UPDATE scoped_grants SET valid = 0, lastValidated = :now WHERE uri = :uri")
    suspend fun invalidate(uri: String, now: Long)
}

@Dao
interface StorageSnapshotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(snapshot: StorageSnapshotEntity)

    @Query("SELECT * FROM storage_snapshots ORDER BY recordedAt DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<StorageSnapshotEntity>>

    @Query("SELECT * FROM storage_snapshots WHERE scope = :scope ORDER BY recordedAt DESC")
    fun getByScope(scope: String): Flow<List<StorageSnapshotEntity>>
}

@Dao
interface PermissionEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: PermissionEventEntity)

    @Query("SELECT * FROM permission_events WHERE permission = :permission ORDER BY eventAt DESC")
    fun getByPermission(permission: String): Flow<List<PermissionEventEntity>>

    @Query("SELECT * FROM permission_events ORDER BY eventAt DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<PermissionEventEntity>>
}

@Dao
interface BatterySampleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sample: BatterySampleEntity)

    @Query("SELECT * FROM battery_samples ORDER BY recordedAt DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<BatterySampleEntity>>
}

@Dao
interface FeatureCapabilityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(capability: FeatureCapabilityEntity)

    @Update
    suspend fun update(capability: FeatureCapabilityEntity)

    @Query("SELECT * FROM feature_capabilities WHERE featureId = :featureId")
    suspend fun getByFeature(featureId: String): FeatureCapabilityEntity?

    @Query("SELECT * FROM feature_capabilities WHERE serviceId = :serviceId")
    fun getByService(serviceId: String): Flow<List<FeatureCapabilityEntity>>

    @Query("UPDATE feature_capabilities SET capabilityStatus = :status, lastChecked = :now, reason = :reason WHERE featureId = :featureId")
    suspend fun updateStatus(featureId: String, status: String, now: Long, reason: String?)
}

// ============================================================================
// DATABASE
// ============================================================================

@Database(
    entities = [
        ScanSessionEntity::class,
        ObservedItemEntity::class,
        OperationRequestEntity::class,
        OperationItemOutcomeEntity::class,
        ScopedGrantEntity::class,
        StorageSnapshotEntity::class,
        PermissionEventEntity::class,
        FeatureCapabilityEntity::class,
        BatterySampleEntity::class,
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanSessionDao(): ScanSessionDao
    abstract fun observedItemDao(): ObservedItemDao
    abstract fun operationRequestDao(): OperationRequestDao
    abstract fun operationItemOutcomeDao(): OperationItemOutcomeDao
    abstract fun scopedGrantDao(): ScopedGrantDao
    abstract fun storageSnapshotDao(): StorageSnapshotDao
    abstract fun permissionEventDao(): PermissionEventDao
    abstract fun featureCapabilityDao(): FeatureCapabilityDao
    abstract fun batterySampleDao(): BatterySampleDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `battery_samples` (
                        `id` TEXT NOT NULL,
                        `levelPercent` INTEGER,
                        `status` TEXT,
                        `temperatureC` REAL,
                        `voltageMv` INTEGER,
                        `technology` TEXT,
                        `charging` INTEGER NOT NULL,
                        `health` TEXT,
                        `recordedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "knoux_audit.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// ============================================================================
// REPOSITORY
// ============================================================================

class KnouxRepository(private val database: AppDatabase) {

    suspend fun createScanSession(
        serviceId: String,
        serviceName: String,
        deviceModel: String?,
        apiLevel: Int?,
    ): String {
        val id = java.util.UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        database.scanSessionDao().insert(
            ScanSessionEntity(
                id = id,
                serviceId = serviceId,
                serviceName = serviceName,
                startedAt = now,
                status = "SCANNING",
                deviceModel = deviceModel,
                apiLevel = apiLevel,
            )
        )
        return id
    }

    suspend fun completeScanSession(
        sessionId: String,
        status: String,
        permissionGranted: Boolean,
        itemsFound: Int,
        bytesFound: Long,
        bytesReclaimable: Long,
    ) {
        val existing = database.scanSessionDao().getById(sessionId) ?: return
        database.scanSessionDao().update(
            existing.copy(
                endedAt = System.currentTimeMillis(),
                status = status,
                permissionGranted = permissionGranted,
                itemsFound = itemsFound,
                bytesFound = bytesFound,
                bytesReclaimable = bytesReclaimable,
            )
        )
    }

    suspend fun recordObservedItems(sessionId: String, items: List<ObservedItemEntity>) {
        val withSession = items.map { it.copy(scanSessionId = sessionId) }
        database.observedItemDao().insertAll(withSession)
    }

    suspend fun createOperationRequest(
        scanSessionId: String,
        serviceId: String,
        requestedAction: String,
        selectionCount: Int,
        keeperIds: List<String>?,
        exclusionIds: List<String>?,
        predictedEligibleBytes: Long?,
    ): String {
        val id = java.util.UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        database.operationRequestDao().insertWithLegacyParent(
            OperationRequestEntity(
                id = id,
                scanSessionId = scanSessionId,
                serviceId = serviceId,
                requestedAction = requestedAction,
                selectionCount = selectionCount,
                keeperIds = keeperIds?.joinToString(","),
                exclusionIds = exclusionIds?.joinToString(","),
                predictedEligibleBytes = predictedEligibleBytes,
                consentGiven = false,
                createdAt = now,
                status = "PENDING",
            )
        )
        return id
    }

    suspend fun recordConsent(operationId: String, granted: Boolean) {
        val existing = database.operationRequestDao().getById(operationId) ?: return
        database.operationRequestDao().insert(
            existing.copy(
                consentGiven = granted,
                consentAt = if (granted) System.currentTimeMillis() else null,
                status = if (granted) "OS_CONFIRMATION" else "CANCELLED",
            )
        )
    }

    suspend fun updateOperationStatus(operationId: String, status: String) {
        val existing = database.operationRequestDao().getById(operationId) ?: return
        database.operationRequestDao().update(
            existing.copy(status = status)
        )
    }

    suspend fun recordItemOutcomes(operationId: String, outcomes: List<OperationItemOutcomeEntity>) {
        val withOp = outcomes.map { it.copy(operationId = operationId) }
        database.operationItemOutcomeDao().insertAll(withOp)
    }

    suspend fun recordScopedGrant(
        id: String,
        uri: String,
        flags: Int,
        persistable: Boolean,
    ) {
        database.scopedGrantDao().insert(
            ScopedGrantEntity(
                id = id,
                uri = uri,
                flags = flags,
                grantedAt = System.currentTimeMillis(),
                persistable = persistable,
                lastValidated = System.currentTimeMillis(),
                valid = true,
            )
        )
    }

    suspend fun invalidateScopedGrant(uri: String) {
        database.scopedGrantDao().invalidate(uri, System.currentTimeMillis())
    }

    suspend fun recordStorageSnapshot(
        id: String,
        totalBytes: Long,
        usedBytes: Long,
        freeBytes: Long,
        mediaBytes: Long,
        cacheBytes: Long,
        otherBytes: Long,
        scope: String,
    ) {
        database.storageSnapshotDao().insert(
            StorageSnapshotEntity(
                id = id,
                totalBytes = totalBytes,
                usedBytes = usedBytes,
                freeBytes = freeBytes,
                mediaBytes = mediaBytes,
                cacheBytes = cacheBytes,
                otherBytes = otherBytes,
                recordedAt = System.currentTimeMillis(),
                scope = scope,
            )
        )
    }

    suspend fun recordPermissionEvent(
        id: String,
        permission: String,
        granted: Boolean,
        source: String = "user",
    ) {
        database.permissionEventDao().insert(
            PermissionEventEntity(
                id = id,
                permission = permission,
                granted = granted,
                eventAt = System.currentTimeMillis(),
                source = source,
            )
        )
    }

    suspend fun recordFeatureCapability(
        featureId: String,
        serviceId: String,
        capabilityStatus: String,
        apiSource: String?,
        permissionRequired: String?,
        reason: String?,
    ) {
        database.featureCapabilityDao().insert(
            FeatureCapabilityEntity(
                featureId = featureId,
                serviceId = serviceId,
                capabilityStatus = capabilityStatus,
                apiSource = apiSource,
                permissionRequired = permissionRequired,
                lastChecked = System.currentTimeMillis(),
                reason = reason,
            )
        )
    }

    suspend fun updateFeatureCapabilityStatus(
        featureId: String,
        status: String,
        reason: String?,
    ) {
        val existing = database.featureCapabilityDao().getByFeature(featureId)
        if (existing != null) {
            database.featureCapabilityDao().updateStatus(
                featureId, status, System.currentTimeMillis(), reason
            )
        } else {
            database.featureCapabilityDao().insert(
                FeatureCapabilityEntity(
                    featureId = featureId,
                    serviceId = "",
                    capabilityStatus = status,
                    apiSource = null,
                    permissionRequired = null,
                    lastChecked = System.currentTimeMillis(),
                    reason = reason,
                )
            )
        }
    }

    // ---------- Audit counters ----------

    suspend fun countSessions(serviceId: String): Int =
        database.scanSessionDao().countByService(serviceId)

    suspend fun countItems(sessionId: String): Int =
        database.observedItemDao().countBySession(sessionId)

    suspend fun countOperations(serviceId: String): Int =
        database.operationRequestDao().countByService(serviceId)

    // ---------- Report flows ----------

    fun recentOperations(limit: Int = 25): Flow<List<OperationRequestEntity>> =
        database.operationRequestDao().getRecent(limit)

    fun allOperations(): Flow<List<OperationRequestEntity>> =
        database.operationRequestDao().getAll()

    fun outcomeSummaries(): Flow<List<OperationOutcomeSummary>> =
        database.operationItemOutcomeDao().summaries()

    fun recentSessions(limit: Int = 25): Flow<List<ScanSessionEntity>> =
        database.scanSessionDao().getRecent(limit)

    suspend fun recordPermissionEvent(
        id: String,
        permission: String,
        granted: Boolean,
        eventAt: Long,
        source: String,
    ) {
        database.permissionEventDao().insert(
            PermissionEventEntity(id, permission, granted, eventAt, source)
        )
    }

    fun recentPermissionEvents(limit: Int = 25): Flow<List<PermissionEventEntity>> =
        database.permissionEventDao().getRecent(limit)

    suspend fun recordBatterySample(
        id: String,
        levelPercent: Int?,
        status: String?,
        temperatureC: Float?,
        voltageMv: Int?,
        technology: String?,
        charging: Boolean,
        health: String?,
        recordedAt: Long,
    ) {
        database.batterySampleDao().insert(
            BatterySampleEntity(
                id, levelPercent, status, temperatureC, voltageMv, technology, charging, health,
                recordedAt,
            )
        )
    }

    fun recentBatterySamples(limit: Int = 50): Flow<List<BatterySampleEntity>> =
        database.batterySampleDao().getRecent(limit)
}
