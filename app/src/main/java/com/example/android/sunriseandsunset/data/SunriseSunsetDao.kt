package com.example.android.sunriseandsunset.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SunriseSunsetDao {
    @Query("SELECT * FROM sunrise_sunset_table")
    fun getAllSunriseSunset(): LiveData<List<SunriseSunset>>

    @Query("SELECT * FROM sunrise_sunset_table WHERE id = :id")
    fun getSunriseSunsetById(id: Long): LiveData<SunriseSunset?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSunriseSunset(sunriseSunset: SunriseSunset)

    @Update
    suspend fun updateSunriseSunset(sunriseSunset: SunriseSunset)

    @Delete
    suspend fun deleteSunriseSunset(sunriseSunset: SunriseSunset)
}