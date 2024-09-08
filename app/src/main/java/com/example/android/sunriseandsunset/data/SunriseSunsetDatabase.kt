package com.example.android.sunriseandsunset.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [SunriseSunset::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SunriseSunsetDatabase: RoomDatabase() {

    abstract fun sunriseSunsetDao(): SunriseSunsetDao

    companion object {
        @Volatile
        private var INSTANCE: SunriseSunsetDatabase? = null

        // Define the migration from version 1 to version 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add the new "position" column with a default value of 0
                db.execSQL("ALTER TABLE sunrise_sunset_table ADD COLUMN position INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): SunriseSunsetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SunriseSunsetDatabase::class.java,
                    "sunrise_sunset_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}