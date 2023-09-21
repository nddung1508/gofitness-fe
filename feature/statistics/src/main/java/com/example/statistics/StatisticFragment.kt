package com.example.statistics

import KcalByDayViewModel
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.statistics.databinding.FragmentStatisticBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticFragment : Fragment() {
    private lateinit var binding : FragmentStatisticBinding
    private lateinit var kcalByDayViewModel: KcalByDayViewModel
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val caloriesData = arrayListOf<BarEntry>()
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

    private fun setUpBarChart() {
        kcalByDayViewModel = ViewModelProvider(this).get(KcalByDayViewModel::class.java)
        val numberOfCalls = 7
        var callsCompleted = 0

        val onLiveDataComplete: () -> Unit = {
            callsCompleted++
            if (callsCompleted == numberOfCalls) {
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
                binding.barChart.xAxis.valueFormatter = CustomDateAxisValueFormatter()
                binding.barChart.xAxis.textSize = 10f
                binding.barChart.invalidate()
            }
        }

        for (i in 6 downTo 0) {
            if (userId != null) {
                kcalByDayViewModel.getKcalByDayForDate(userId, getCurrentDateFormat(System.currentTimeMillis()))
                    .observe(viewLifecycleOwner) { listKcalByDay ->
                        val yValue = listKcalByDay.sumOf { it.kcal }.toFloat()
                        val xValue = i.toFloat()
                        caloriesData.add(BarEntry(xValue, yValue))
                        onLiveDataComplete() // Call the callback when LiveData completes
                    }
            }
        }
    }

    private fun getCurrentDateFormat(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    companion object{
        fun newInstance(): StatisticFragment {
            return StatisticFragment()
        }
        private const val LINE_CHART_DATE_FORMAT = "yyyy-MM-dd"
    }
}