package com.project.dockin.data.db

import androidx.room.*

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