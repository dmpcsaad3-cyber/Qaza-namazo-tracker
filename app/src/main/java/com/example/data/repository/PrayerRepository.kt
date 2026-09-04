package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.PrayerDao
import com.example.data.model.AppSettings
import com.example.data.model.HistoryLog
import com.example.data.model.PrayerRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PrayerRepository(private val prayerDao: PrayerDao) {

    val allPrayers: Flow<List<PrayerRecord>> = prayerDao.getAllPrayers()
    val recentHistory: Flow<List<HistoryLog>> = prayerDao.getRecentHistory()
    val allSettings: Flow<List<AppSettings>> = prayerDao.getAllSettings()

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val existing = prayerDao.getPrayerById("fajr")
        if (existing == null) {
            prayerDao.insertPrayers(AppDatabase.INITIAL_PRAYERS)
        }
    }

    suspend fun markPrayerOffered(prayerId: String, prayerName: String, count: Int = 1) = withContext(Dispatchers.IO) {
        prayerDao.markPrayed(prayerId, count)
        prayerDao.insertHistory(
            HistoryLog(
                prayerId = prayerId,
                prayerName = prayerName,
                amountChanged = count,
                type = "PRAYED",
                note = "$count $prayerName qaza ada ki"
            )
        )
    }

    suspend fun addMissedPrayer(prayerId: String, prayerName: String, count: Int = 1) = withContext(Dispatchers.IO) {
        prayerDao.addMissed(prayerId, count)
        prayerDao.insertHistory(
            HistoryLog(
                prayerId = prayerId,
                prayerName = prayerName,
                amountChanged = count,
                type = "MISSED_ADDED",
                note = "$count $prayerName qaza shamil ki"
            )
        )
    }

    suspend fun updatePrayerCounts(prayerId: String, prayerName: String, remaining: Int, completed: Int) = withContext(Dispatchers.IO) {
        prayerDao.setCounts(prayerId, remaining.coerceAtLeast(0), completed.coerceAtLeast(0))
        prayerDao.insertHistory(
            HistoryLog(
                prayerId = prayerId,
                prayerName = prayerName,
                amountChanged = remaining,
                type = "ADJUSTED",
                note = "Count adjusted manually to $remaining remaining"
            )
        )
    }

    suspend fun markFullDayOffered() = withContext(Dispatchers.IO) {
        prayerDao.markAllPrayedOneDay()
        prayerDao.insertHistory(
            HistoryLog(
                prayerId = "ALL",
                prayerName = "All 6 Prayers (1 Day)",
                amountChanged = 1,
                type = "PRAYED",
                note = "1 mukammal din ki qaza namazen ada keen"
            )
        )
    }

    suspend fun addFullDayMissed() = withContext(Dispatchers.IO) {
        prayerDao.addMissedOneDay()
        prayerDao.insertHistory(
            HistoryLog(
                prayerId = "ALL",
                prayerName = "All 6 Prayers (1 Day)",
                amountChanged = 1,
                type = "MISSED_ADDED",
                note = "1 din ki qaza namazen shamil keen"
            )
        )
    }

    suspend fun applyBulkCalculation(totalDays: Int, overwrite: Boolean) = withContext(Dispatchers.IO) {
        if (overwrite) {
            prayerDao.setRemainingToAll(totalDays)
        } else {
            prayerDao.addMissedDaysToAll(totalDays)
        }
        prayerDao.insertHistory(
            HistoryLog(
                prayerId = "ALL",
                prayerName = "Calculator Entry",
                amountChanged = totalDays,
                type = "BULK_CALCULATED",
                note = "$totalDays dino ($totalDays days) ki qaza calculate ker k shamil ki"
            )
        )
    }

    suspend fun resetAllCounts() = withContext(Dispatchers.IO) {
        prayerDao.resetAllCounts()
        prayerDao.insertHistory(
            HistoryLog(
                prayerId = "ALL",
                prayerName = "All Prayers",
                amountChanged = 0,
                type = "ADJUSTED",
                note = "Tamam record reset kar diya gaya"
            )
        )
    }

    suspend fun deleteHistoryLog(log: HistoryLog) = withContext(Dispatchers.IO) {
        prayerDao.deleteHistory(log)
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        prayerDao.clearHistory()
    }

    suspend fun setSetting(key: String, value: String) = withContext(Dispatchers.IO) {
        prayerDao.saveSetting(AppSettings(key, value))
    }

    suspend fun getSetting(key: String): String? = withContext(Dispatchers.IO) {
        prayerDao.getSetting(key)
    }
}
