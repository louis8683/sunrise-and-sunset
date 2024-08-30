package com.example.android.sunriseandsunset

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.NumberFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@BindingAdapter("formattedDouble")
fun TextView.bindFormattedDouble(number: Double?) {
    number?.let {
        val format = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 1
            maximumFractionDigits = 1
        }
        text = format.format(it)
    }
}

@BindingAdapter("formattedLocalTime")
fun TextView.bindFormattedLocalTime(time: LocalTime?) {
    time?.let {
        val formatter = DateTimeFormatter.ofPattern("h:mm:ss a")
        text = time.format(formatter)
    }
}
