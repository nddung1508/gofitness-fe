package com.example.statistics

import KcalByDayViewModel
import StepViewModel
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.statistics.databinding.FragmentStatisticBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class StatisticFragment : Fragment() {
    private lateinit var binding : FragmentStatisticBinding
    private lateinit var kcalByDayViewModel: KcalByDayViewModel
    private lateinit var stepViewModel: StepViewModel
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val caloriesData = arrayListOf<BarEntry>()
    private val stepData = arrayListOf<BarEntry>()
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
    }

    override fun onStart() {
        super.onStart()
        setUpKcalBarChart()
        setUpStepBarChart()
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

    private fun setUpKcalBarChart() {
        kcalByDayViewModel = ViewModelProvider(this).get(KcalByDayViewModel::class.java)
        val numberOfCalls = 7
        var callsCompleted = 0

        val onLiveDataComplete: () -> Unit = {
            callsCompleted++
            if (callsCompleted == numberOfCalls) {
                val dataSet = BarDataSet(caloriesData, "Calories Burned")
                dataSet.setDrawValues(false)
                dataSet.colors = listOf(Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE, requireContext().getColor(R.color.purpleVictoria),
                    requireContext().getColor(R.color.Orange), requireContext().getColor(R.color.Chablis))
                val barData = BarData(dataSet)
                binding.barChartKcal.data = barData
                binding.barChartKcal.data.isHighlightEnabled = false
                binding.barChartKcal.isDoubleTapToZoomEnabled = false
                binding.barChartKcal.axisLeft.setDrawGridLines(false)
                binding.barChartKcal.axisRight.setDrawGridLines(false)
                binding.barChartKcal.xAxis.setDrawGridLines(false)
                binding.barChartKcal.xAxis.position = XAxis.XAxisPosition.BOTTOM
                binding.barChartKcal.axisRight.setDrawLabels(false)
                binding.barChartKcal.description.isEnabled = false
                binding.barChartKcal.legend.isEnabled = false
                binding.barChartKcal.xAxis.granularity = 1f
                binding.barChartKcal.axisLeft.axisMinimum = 0f
                binding.barChartKcal.xAxis.valueFormatter = CustomDateAxisValueFormatter()
                binding.barChartKcal.xAxis.textSize = 10f
                binding.barChartStep.animateY(1000)
                binding.barChartKcal.invalidate()
            }
        }

        for (i in 6 downTo 0) {
            if (userId != null) {
                kcalByDayViewModel.getKcalByDayForDate(userId, getCurrentDateFormat(System.currentTimeMillis() - reverseInt(i) * 86400000))
                    .observe(viewLifecycleOwner) { listKcalByDay ->
                        val yValue = listKcalByDay.sumOf { it.kcal }.toFloat()
                        val xValue = i.toFloat()
                        caloriesData.add(BarEntry(xValue, yValue))
                        onLiveDataComplete()
                    }
            }
        }
    }

    private fun setUpStepBarChart(){
        stepViewModel = ViewModelProvider(this).get(StepViewModel::class.java)
        val numberOfCalls = 7
        var callsCompleted = 0

        val onLiveDataComplete: () -> Unit = {
            callsCompleted++
            if (callsCompleted == numberOfCalls) {
                val dataSet = BarDataSet(stepData, "Calories Burned")
                dataSet.setDrawValues(false)
                dataSet.colors = listOf(
                    Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE, requireContext().getColor(R.color.purpleVictoria),
                    requireContext().getColor(R.color.Orange), requireContext().getColor(R.color.Chablis))
                val barData = BarData(dataSet)
                binding.barChartStep.data = barData
                binding.barChartStep.data.isHighlightEnabled = false
                binding.barChartStep.isDoubleTapToZoomEnabled = false
                binding.barChartStep.axisLeft.setDrawGridLines(false)
                binding.barChartStep.axisRight.setDrawGridLines(false)
                binding.barChartStep.xAxis.setDrawGridLines(false)
                binding.barChartStep.xAxis.position = XAxis.XAxisPosition.BOTTOM
                binding.barChartStep.axisRight.setDrawLabels(false)
                binding.barChartStep.description.isEnabled = false
                binding.barChartStep.legend.isEnabled = false
                binding.barChartStep.xAxis.granularity = 1f
                binding.barChartStep.axisLeft.axisMinimum = 0f
                binding.barChartStep.xAxis.valueFormatter = CustomDateAxisValueFormatter()
                binding.barChartStep.xAxis.textSize = 10f
                binding.barChartStep.animateY(1000)
                binding.barChartStep.invalidate()
            }
        }

        for (i in 6 downTo 0) {
            if (userId != null) {
                stepViewModel.getStepsForDate(userId, getCurrentDateFormat(System.currentTimeMillis() - reverseInt(i) * 86400000))
                    .observe(viewLifecycleOwner) { listKcalByDay ->
                        val yValue = listKcalByDay.sumOf { it.amount }.toFloat()
                        val xValue = i.toFloat()
                        stepData.add(BarEntry(xValue, yValue))
                        onLiveDataComplete()
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

    private fun reverseInt(int: Int) : Int{
        return when (int){
            6 -> 0
            5 -> 1
            4 -> 2
            3 -> 3
            2 -> 4
            1 -> 5
            0 -> 6
            else -> 0
        }
    }

    companion object{
        fun newInstance(): StatisticFragment {
            return StatisticFragment()
        }
        private const val LINE_CHART_DATE_FORMAT = "yyyy-MM-dd"
    }
}