package com.example.android.sunriseandsunset.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * SunriseSunset represents a sunrise and sunset location to be displayed.
 *
 * @property locationName The name of the location.
 * @property latitude The latitude of the location.
 * @property longitude The longitude of the location.
 * @property sunrise The time of sunrise at the location.
 * @property sunset The time of sunset at the location.
 */
@Parcelize
@Entity(tableName = "sunrise_sunset_table")
data class SunriseSunset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val sunrise: LocalTime,
    val sunset: LocalTime,

    var position: Int // This field stores the item order
) : Parcelable {
    /**
     * Formats the sunrise time to a string in "HH:mm" format.
     *
     * @return A string representation of the sunrise time in "HH:mm" format.
     */
    fun formattedSunriseTime(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return sunrise.format(formatter)
    }

    /**
     * Formats the sunset time to a string in "HH:mm" format.
     *
     * @return A string representation of the sunset time in "HH:mm" format.
     */
    fun formattedSunsetTime(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return sunset.format(formatter)
    }
}