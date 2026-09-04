package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_logs")
data class HistoryLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prayerId: String,
    val prayerName: String,
    val amountChanged: Int,
    val type: String, // PRAYED, MISSED_ADDED, ADJUSTED, BULK_CALCULATED
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
