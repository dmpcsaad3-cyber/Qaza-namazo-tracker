package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppSettings
import com.example.data.model.HistoryLog
import com.example.data.model.PrayerRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {

    @Query("SELECT * FROM prayer_records ORDER BY orderIndex ASC")
    fun getAllPrayers(): Flow<List<PrayerRecord>>

    @Query("SELECT * FROM prayer_records WHERE prayerId = :id")
    suspend fun getPrayerById(id: String): PrayerRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayers(prayers: List<PrayerRecord>)

    @Update
    suspend fun updatePrayer(prayer: PrayerRecord)

    @Query("UPDATE prayer_records SET remainingCount = MAX(0, remainingCount - :count), completedCount = completedCount + :count WHERE prayerId = :id")
    suspend fun markPrayed(id: String, count: Int)

    @Query("UPDATE prayer_records SET remainingCount = remainingCount + :count WHERE prayerId = :id")
    suspend fun addMissed(id: String, count: Int)

    @Query("UPDATE prayer_records SET remainingCount = :remaining, completedCount = :completed WHERE prayerId = :id")
    suspend fun setCounts(id: String, remaining: Int, completed: Int)

    @Query("UPDATE prayer_records SET remainingCount = MAX(0, remainingCount - 1), completedCount = completedCount + 1")
    suspend fun markAllPrayedOneDay()

    @Query("UPDATE prayer_records SET remainingCount = remainingCount + 1")
    suspend fun addMissedOneDay()

    @Query("UPDATE prayer_records SET remainingCount = remainingCount + :days")
    suspend fun addMissedDaysToAll(days: Int)

    @Query("UPDATE prayer_records SET remainingCount = :count")
    suspend fun setRemainingToAll(count: Int)

    @Query("UPDATE prayer_records SET remainingCount = 0, completedCount = 0")
    suspend fun resetAllCounts()

    // History Log
    @Query("SELECT * FROM history_logs ORDER BY timestamp DESC LIMIT 60")
    fun getRecentHistory(): Flow<List<HistoryLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(log: HistoryLog)

    @Delete
    suspend fun deleteHistory(log: HistoryLog)

    @Query("DELETE FROM history_logs")
    suspend fun clearHistory()

    // App Settings
    @Query("SELECT value FROM app_settings WHERE `key` = :key")
    suspend fun getSetting(key: String): String?

    @Query("SELECT * FROM app_settings")
    fun getAllSettings(): Flow<List<AppSettings>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: AppSettings)
}
