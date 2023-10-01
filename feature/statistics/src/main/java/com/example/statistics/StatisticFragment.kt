package com.example.statistics

import KcalByDayViewModel
import StepViewModel
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class StatisticFragment : Fragment() {
    private var client = OkHttpClient()
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
        val sharedPreferences =
            requireContext().getSharedPreferences(
                "SharedPreferences",
                Context.MODE_PRIVATE
            )
        setUpTab()
        binding.tabs.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { position ->
                    when (position) {
                        0 -> {
                            binding.clTips.visibility = View.GONE
                            binding.clStatistics.visibility = View.VISIBLE
                        }
                        1 -> {
                            binding.clTips.visibility= View.VISIBLE
                            binding.clStatistics.visibility = View.GONE
                        }
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        val height = binding.tvHeight.text
        val weight = binding.tvWeight.text
        var aimWeight = "0"
        var duration = "0"
        val numOfWeek = duration.toFloat()/7
        val savedText = sharedPreferences.getString("current_tip", "")
        binding.tvResponse.text = savedText
        binding.btnGetTip.isEnabled = (binding.etAimWeight.text?.isNotEmpty() == true && binding.etDuration.text?.isNotEmpty() == true)
        binding.etAimWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0: Editable) {
                binding.btnGetTip.isEnabled = (binding.etAimWeight.text?.isNotEmpty() == true && binding.etDuration.text?.isNotEmpty() == true)
                aimWeight = binding.etAimWeight.text.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        binding.etDuration.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0: Editable) {
                binding.btnGetTip.isEnabled = (binding.etAimWeight.text?.isNotEmpty() == true && binding.etDuration.text?.isNotEmpty() == true)
                duration = binding.etDuration.text.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.btnGetTip.setOnClickListener {
            if(duration.toInt() > 365 || aimWeight.toInt() < 30){
                binding.tvResponse.text = "The input amount is not valid, the weight is either too low or duration is too high"
            }
            else{
                val question = if (numOfWeek > 1) {
                    "Can i get a weekly (week 1, ..., week $numOfWeek) diet for an ${height}cm height and $weight kg weight person, which goal is to lose weight to $aimWeight kg in $duration days?"
                } else {
                    "Can i get a daily diet for an ${height}cm height and $weight kg weight person, which goal is to lose weight to $aimWeight kg in $duration days?"
                }
                getResponse(question) { response ->
                    requireActivity().runOnUiThread {
                        binding.tvResponse.text = response
                        val editor = sharedPreferences.edit()
                        editor.putString("current_tip", response)
                        editor.apply()
                    }
                }
            }
        }
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
    private fun getResponse(tip: String, callback: (String) -> Unit) {
        val apiKey = BuildConfig.API_KEY
        val url = "https://api.openai.com/v1/completions"
        val requestBody = """
            {
            "model" : "text-davinci-003",
            "prompt": "$tip",
            "max_tokens": 1000,
            "temperature": 0
            }
        """.trimIndent()
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
        if (apiKey.isNotEmpty()) {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("Calling Error", "Failed on calling ChatGPT API", e)

                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    if (body != null) {
                        Log.v("data", body)
                    } else {
                        Log.v("data", "The data of the body empty")
                    }
                    val jsonObject = JSONObject(body)
                    val jsonArray: JSONArray = jsonObject.getJSONArray("choices")
                    val textResult = jsonArray.getJSONObject(0).getString("text")
                    callback(textResult)
                }
            })
        }
    }

    companion object{
        fun newInstance(): StatisticFragment {
            return StatisticFragment()
        }
    }
}