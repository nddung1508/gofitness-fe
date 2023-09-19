package com.example.statistics

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomDateAxisValueFormatter(private val daysOfWeek: Array<String>) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val timestamp = value.toLong()
        val dayOfWeekAbbreviation = SimpleDateFormat("E", Locale.getDefault()).format(Date(timestamp))
        if (dayOfWeekAbbreviation == daysOfWeek[daysOfWeek.size - 1]) {
            return "today"
        }
        return dayOfWeekAbbreviation
    }
}