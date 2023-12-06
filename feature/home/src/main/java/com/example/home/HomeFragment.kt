package com.example.home

import StepViewModel
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.home.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.sqrt

class HomeFragment : Fragment(), SensorEventListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null
    private var stepSensor: Sensor? = null
    private var stepsSinceReboot: Int? = 0
    private lateinit var stepViewModel: StepViewModel
    private val minUpdateInterval = 3000
    private var lastSensorUpdateTime: Long = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        stepViewModel = ViewModelProvider(this)[StepViewModel::class.java]
        checkActivityRecognitionPermission()
        setUpSensor()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerSensor()
    }

    private fun setUpSensor() {
        stepViewModel = ViewModelProvider(this)[StepViewModel::class.java]
        val currentUser = FirebaseAuth.getInstance().currentUser
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
        binding.clStartWorkout.setOnClickListener {
            val intent = Intent(requireContext(), ExerciseActivity::class.java)
            startActivity(intent)
        }
        if (currentUser != null) {
            stepViewModel.getStepsForDate(
                currentUser.uid,
                getCurrentDateFormat(System.currentTimeMillis())
            ).observe(viewLifecycleOwner) { steps ->
                if (steps.isNotEmpty()) {
                    stepsSinceReboot = steps.last().amount
                    binding.tvStepsNumber.text = steps.last().amount.toString()
                    binding.tvDailySteps.text = steps.last().amount.toString() + "/5000"
                    val progress = steps.last().amount.toFloat().div(5000).times(100)
                    getKcalBurned(steps.last().amount)
                    binding.pbSteps.progress = progress.toInt()
                } else {
                    stepsSinceReboot = 0
                    binding.tvStepsNumber.text = 0.toString()
                    binding.tvDailySteps.text = "0/5000"
                    binding.pbSteps.progress = 0
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastSensorUpdateTime < minUpdateInterval) {
            return
        }

        lastSensorUpdateTime = currentTime

        if (stepSensor == null && event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val xAxis = event.values[0]
            val yAxis = event.values[1]
            val zAxis = event.values[2]
            val acceleration = sqrt(xAxis * xAxis + yAxis * yAxis + zAxis * zAxis)
            if (acceleration > ACCELERATION_OF_ONE_STEP) {
                stepsSinceReboot = stepsSinceReboot!! + 1
                binding.tvStepsNumber.text = stepsSinceReboot.toString()
                stepsSinceReboot?.let {
                    stepViewModel.addStep(it)
                    val progress = it.toFloat().div(5000).times(100)
                    binding.pbSteps.progress = progress.toInt()
                }
            }
        } else if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER && event.values.isNotEmpty()) {
                    stepsSinceReboot = event.values[0].toInt()
                    stepsSinceReboot?.let {
                        stepViewModel.addStep(it)
                        binding.tvStepsNumber.text = it.toString()
                        val progress = it.toFloat().div(5000).times(100)
                        binding.pbSteps.progress = progress.toInt()
                    }
        } else {
            Log.d("SensorDebug", "Unknown sensor type: ${event?.sensor?.type}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun getKcalBurned(step: Int) {
        val kcalBurned = (step * STEP_CALORIES_FACTOR)
        val formattedKcalString = String.format("%.2f", kcalBurned)
        binding.tvKcal.text = formattedKcalString
    }

    private fun getCurrentDateFormat(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun checkActivityRecognitionPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION
            )
        }
    }

    private fun registerSensor() {
        if (stepSensor == null && accelerometerSensor != null) {
            sensorManager.registerListener(
                this,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        } else if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
        private const val MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 1
        private const val ACCELERATION_OF_ONE_STEP = 10
        const val STEP_CALORIES_FACTOR = 0.035
    }
}