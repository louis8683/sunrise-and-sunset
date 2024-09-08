package com.example.android.sunriseandsunset.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface SunriseSunsetDao {
    @Query("SELECT * FROM sunrise_sunset_table ORDER BY position ASC")
    fun getAllSunriseSunset(): LiveData<List<SunriseSunset>>

    @Query("SELECT * FROM sunrise_sunset_table WHERE id = :id")
    fun getSunriseSunsetById(id: Long): LiveData<SunriseSunset?>

    @Query("SELECT * FROM sunrise_sunset_table ORDER BY position ASC LIMIT 1")
    fun getFirstSunriseSunset(): LiveData<SunriseSunset?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSunriseSunset(sunriseSunset: SunriseSunset)

    @Update
    suspend fun updateSunriseSunset(sunriseSunset: SunriseSunset)

    @Delete
    suspend fun deleteSunriseSunset(sunriseSunset: SunriseSunset)

    // Optional: Update the position of a specific item
    @Query("UPDATE sunrise_sunset_table SET position = :position WHERE id = :id")
    suspend fun updatePosition(id: Long, position: Int)

    // Optional: Update all items with new positions (e.g., after reordering)
    @Transaction
    suspend fun updateItemPositions(items: List<SunriseSunset>) {
        items.forEachIndexed { index, item ->
            updatePosition(item.id, index)
        }
    }
}