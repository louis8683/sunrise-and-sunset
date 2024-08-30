package com.example.android.sunriseandsunset.network

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class Results(
    val date: String,
    val sunrise: String,
    val sunset: String,
    val first_light: String,
    val last_light: String,
    val dawn: String,
    val dusk: String,
    val solar_noon: String,
    val golden_hour: String,
    val day_length: String,
    val timezone: String,
    val utc_offset: Int
) {

    fun parsedSunrise() = parseTime(sunrise)
    fun parsedSunset() = parseTime(sunset)

    private fun parseTime(timeString: String): LocalTime? {
        val formatter = DateTimeFormatter.ofPattern("h:mm:ss a")

        return try {
            LocalTime.parse(timeString, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }
}