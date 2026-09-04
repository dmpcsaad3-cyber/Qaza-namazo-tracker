package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.HistoryLog
import com.example.data.model.PrayerRecord
import com.example.data.repository.PrayerRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class DashboardSummary(
    val totalRemaining: Int = 0,
    val totalCompleted: Int = 0,
    val totalCombined: Int = 0,
    val progressPercentage: Float = 0f,
    val todayOfferedCount: Int = 0,
    val dailyGoal: Int = 6,
    val estimatedDaysRemaining: Int = 0
)

class PrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PrayerRepository

    val prayers: StateFlow<List<PrayerRecord>>
    val history: StateFlow<List<HistoryLog>>

    private val _dailyGoal = MutableStateFlow(6)
    val dailyGoal: StateFlow<Int> = _dailyGoal.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = PrayerRepository(database.prayerDao())

        prayers = repository.allPrayers.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        history = repository.recentHistory.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.checkAndSeedInitialData()
            val savedGoal = repository.getSetting("daily_goal")
            if (savedGoal != null) {
                _dailyGoal.value = savedGoal.toIntOrNull() ?: 6
            }
        }
    }

    val summary: StateFlow<DashboardSummary> = combine(
        prayers,
        history,
        dailyGoal
    ) { prayerList, historyList, goal ->
        val remaining = prayerList.sumOf { it.remainingCount }
        val completed = prayerList.sumOf { it.completedCount }
        val total = remaining + completed
        val progress = if (total > 0) (completed.toFloat() / total.toFloat()) else 0f

        // Count prayers offered today
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todayOffered = historyList
            .filter { it.timestamp >= startOfToday && it.type == "PRAYED" }
            .sumOf { it.amountChanged }

        val estDays = if (goal > 0 && remaining > 0) {
            (remaining + goal - 1) / goal
        } else {
            0
        }

        DashboardSummary(
            totalRemaining = remaining,
            totalCompleted = completed,
            totalCombined = total,
            progressPercentage = progress,
            todayOfferedCount = todayOffered,
            dailyGoal = goal,
            estimatedDaysRemaining = estDays
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardSummary()
    )

    fun markOffered(prayer: PrayerRecord, count: Int = 1) {
        if (prayer.remainingCount <= 0 && count > 0) {
            viewModelScope.launch {
                _userMessage.emit("${prayer.urduName} ki koi qaza baqi nahi hai! Masha'Allah")
            }
            return
        }
        viewModelScope.launch {
            val actualCount = count.coerceAtMost(prayer.remainingCount)
            repository.markPrayerOffered(prayer.prayerId, prayer.urduName, actualCount)
            _userMessage.emit("ما شاء الله! $actualCount ${prayer.urduName} qaza ada ki gai")
        }
    }

    fun addMissed(prayer: PrayerRecord, count: Int = 1) {
        viewModelScope.launch {
            repository.addMissedPrayer(prayer.prayerId, prayer.urduName, count)
            _userMessage.emit("$count ${prayer.urduName} qaza record m shamil ki gai")
        }
    }

    fun markFullDayOffered() {
        val prayerList = prayers.value
        if (prayerList.any { it.remainingCount > 0 }) {
            viewModelScope.launch {
                repository.markFullDayOffered()
                _userMessage.emit("الحمد لله! 1 din ki tamam 6 qaza namazen ada ki gaeen")
            }
        } else {
            viewModelScope.launch {
                _userMessage.emit("Masha'Allah! Koi qaza namaz baqi nahi hai")
            }
        }
    }

    fun addFullDayMissed() {
        viewModelScope.launch {
            repository.addFullDayMissed()
            _userMessage.emit("1 din ki tamam 6 qaza namazen shamil ki gaeen")
        }
    }

    fun updatePrayerCounts(prayer: PrayerRecord, remaining: Int, completed: Int) {
        viewModelScope.launch {
            repository.updatePrayerCounts(prayer.prayerId, prayer.urduName, remaining, completed)
            _userMessage.emit("${prayer.urduName} ka record update ho gaya")
        }
    }

    fun calculateAndApplyQaza(
        years: Int,
        months: Int,
        days: Int,
        exemptedDays: Int = 0,
        overwrite: Boolean = false
    ) {
        val totalDays = (years * 365) + (months * 30) + days - exemptedDays
        val finalDays = totalDays.coerceAtLeast(0)
        viewModelScope.launch {
            repository.applyBulkCalculation(finalDays, overwrite)
            _userMessage.emit("$finalDays dino ($finalDays days) ki qaza namazen kamyabi se darj ho gaeen")
        }
    }

    fun setDailyGoal(goal: Int) {
        val newGoal = goal.coerceAtLeast(1)
        _dailyGoal.value = newGoal
        viewModelScope.launch {
            repository.setSetting("daily_goal", newGoal.toString())
            _userMessage.emit("Daily target $newGoal qaza namazen set ho gaya")
        }
    }

    fun resetAll() {
        viewModelScope.launch {
            repository.resetAllCounts()
            _userMessage.emit("Tamam qaza record reset kar diya gaya")
        }
    }

    fun deleteHistoryItem(log: HistoryLog) {
        viewModelScope.launch {
            repository.deleteHistoryLog(log)
            _userMessage.emit("Log entry delete ho gai")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
            _userMessage.emit("Tamam history clear ho gai")
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PrayerViewModel::class.java)) {
                return PrayerViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
