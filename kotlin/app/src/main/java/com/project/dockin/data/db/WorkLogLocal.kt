package com.project.dockin.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "worklogs")
data class WorkLogLocal(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val serverId: Long? = null,
    val title: String,
    val content: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val syncState: String = "pending"   // pending | synced | failed
)