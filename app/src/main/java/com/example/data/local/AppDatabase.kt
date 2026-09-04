package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AppSettings
import com.example.data.model.HistoryLog
import com.example.data.model.PrayerRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [PrayerRecord::class, HistoryLog::class, AppSettings::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun prayerDao(): PrayerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val INITIAL_PRAYERS = listOf(
            PrayerRecord("fajr", "Fajr", "فجر", "الفجر", 2, "Farz", 0, 0, 1),
            PrayerRecord("dhuhr", "Dhuhr", "ظہر", "الظهر", 4, "Farz", 0, 0, 2),
            PrayerRecord("asr", "Asr", "عصر", "العصر", 4, "Farz", 0, 0, 3),
            PrayerRecord("maghrib", "Maghrib", "مغرب", "المغرب", 3, "Farz", 0, 0, 4),
            PrayerRecord("isha", "Isha", "عشاء", "العشاء", 4, "Farz", 0, 0, 5),
            PrayerRecord("witr", "Witr", "وتر", "الوتر", 3, "Wajib", 0, 0, 6)
        )

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "qaza_namaz_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            scope.launch(Dispatchers.IO) {
                                getDatabase(context, scope).prayerDao().insertPrayers(INITIAL_PRAYERS)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
