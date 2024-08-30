package com.example.android.sunriseandsunset.data

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteOpenHelper

@Database(entities = [SunriseSunset::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SunriseSunsetDatabase: RoomDatabase() {

    abstract fun sunriseSunsetDao(): SunriseSunsetDao

    companion object {
        @Volatile
        private var INSTANCE: SunriseSunsetDatabase? = null

        fun getDatabase(context: Context): SunriseSunsetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SunriseSunsetDatabase::class.java,
                    "sunrise_sunset_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}