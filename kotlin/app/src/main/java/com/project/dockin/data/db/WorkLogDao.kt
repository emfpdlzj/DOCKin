package com.project.dockin.data.db

import androidx.room.*

@Entity(tableName = "worklogs")
data class WorkLogLocal(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,   // ✅ 여기!
    val serverId: Long? = null,
    val title: String,
    val content: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val syncState: String = "pending"
)

@Dao
interface WorkLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(e: WorkLogLocal): Long

    @Query("SELECT * FROM worklogs ORDER BY updatedAt DESC")
    fun observeAll(): kotlinx.coroutines.flow.Flow<List<WorkLogLocal>>

    @Query("SELECT * FROM worklogs WHERE syncState='pending' OR syncState='failed'")
    suspend fun findPending(): List<WorkLogLocal>

    @Query("UPDATE worklogs SET syncState=:state, serverId=:serverId WHERE localId=:localId")
    suspend fun mark(localId: Long, state: String, serverId: Long?)
}