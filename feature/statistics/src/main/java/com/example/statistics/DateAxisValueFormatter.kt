package com.example.statistics

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CustomDateAxisValueFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = currentDate

        val daysAgo = 6 - value.toInt() // Calculate days ago from the value

        return if (daysAgo == 0) {
            "Today"
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
            dateFormat.format(calendar.time)
        }
    }
}