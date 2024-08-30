package com.example.android.sunriseandsunset.network



data class SunriseSunsetResponse(
    val results: Results,
    val status: String
) {
    companion object {
        const val STATUS_OK = "OK"
        const val STATUS_ERROR = "ERROR"
    }
}