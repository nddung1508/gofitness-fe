package com.example.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.statistics.databinding.FragmentStatisticBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticFragment : Fragment() {
    private lateinit var binding : FragmentStatisticBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTab()
        setUpBarChart()
    }
    private fun setUpTab() {
        if (binding.tabs.tabCount == 0) {
            binding.tabs.addTab(
                binding.tabs.newTab().setText("Statistics")
            )
            binding.tabs.addTab(
                binding.tabs.newTab().setText("Tips")
            )
        }
    }

    private fun setUpBarChart(){
        val caloriesData = ArrayList<BarEntry>()
        val daysOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        val currentTimeMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        val last7DaysTimestamps = ArrayList<Long>()
        for (i in 6 downTo 0) {
            calendar.timeInMillis = currentTimeMillis
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            last7DaysTimestamps.add(calendar.timeInMillis)
        }

        for ((index, timestamp) in last7DaysTimestamps.withIndex()) {
            val xValue = index.toFloat()
            val yValue = 100f// Implement this function
            caloriesData.add(BarEntry(xValue, yValue))
        }

        val dataSet = BarDataSet(caloriesData, "Calories Burned")
        dataSet.setDrawValues(false)
        val barData = BarData(dataSet)
        binding.barChart.data = barData
        binding.barChart.data.isHighlightEnabled = false
        binding.barChart.isDoubleTapToZoomEnabled = false
        binding.barChart.axisLeft.setDrawGridLines(false)
        binding.barChart.axisRight.setDrawGridLines(false)
        binding.barChart.xAxis.setDrawGridLines(false)
        binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.barChart.axisRight.setDrawLabels(false)
        binding.barChart.description.isEnabled = false
        binding.barChart.legend.isEnabled = false
        binding.barChart.xAxis.granularity = 1f
        binding.barChart.axisLeft.axisMinimum = 0f
        binding.barChart.xAxis.valueFormatter = CustomDateAxisValueFormatter(daysOfWeek)
        binding.barChart.xAxis.textSize = 10f
        binding.barChart.invalidate()
    }

//    private fun getKcalValueForTimestamp(kcalData: List<KcalData>, timestamp: Long): Float {
//        for (data in kcalData) {
//            if (data.timestamp == timestamp) {
//                return data.kcalValue.toFloat()
//            }
//        }
//        return 0f // Return a default value if not found.
//    }

    companion object{
        fun newInstance(): StatisticFragment {
            return StatisticFragment()
        }
        private const val LINE_CHART_DATE_FORMAT = "yyyy-MM-dd"
    }
}