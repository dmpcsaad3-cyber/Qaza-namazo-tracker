package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_records")
data class PrayerRecord(
    @PrimaryKey
    val prayerId: String,
    val englishName: String,
    val urduName: String,
    val arabicName: String,
    val rakats: Int,
    val rakatType: String,
    val remainingCount: Int,
    val completedCount: Int,
    val orderIndex: Int
)
