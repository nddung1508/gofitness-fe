package com.example.home

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.home.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.sqrt

class HomeFragment: Fragment(), SensorEventListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null
    private var stepSensor: Sensor? = null
    private var stepsSinceReboot : Int ? = 0
    private val userWeightInKg = 70.0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogout.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if(currentUser != null){
                FirebaseAuth.getInstance().signOut()
                Thread.sleep(1500)
            }
        }

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if(stepSensor == null){
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
        binding.tvStepsNumber.text = stepsSinceReboot.toString()
        getKcalBurned()
        //Start workout
        binding.clStartWorkout.setOnClickListener {
            val intent = Intent(requireContext(), ExerciseActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(stepSensor==null){
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                val xAxis = event.values[0]
                val yAxis = event.values[1]
                val zAxis = event.values[2]

                val acceleration = sqrt(xAxis * xAxis + yAxis * yAxis + zAxis * zAxis)
                if (acceleration > 10) {
                    stepsSinceReboot = stepsSinceReboot!! + 1
                    binding.tvStepsNumber.text = stepsSinceReboot.toString()
                    getKcalBurned()
                    stepsSinceReboot?.let{
                        binding.pbSteps.progress = it/5000 * 100
                    }
                }
            }
        }
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            Toast.makeText(requireContext(), "Hello", Toast.LENGTH_SHORT).show()
            binding.tvStepsNumber.text = event.values[0].toInt().toString()
            stepsSinceReboot =  event.values[0].toInt()
            stepsSinceReboot?.let {
                val progress = it.toFloat().div(5000).times(100)
                if (progress != null) {
                    binding.pbSteps.progress = progress.toInt()
                }
            }
            getKcalBurned()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun getKcalBurned(){
        stepsSinceReboot?.let {
            val kcalBurned = (it * userWeightInKg * STEP_CALORIES_FACTOR) / 1000
            binding.tvKcal.text = kcalBurned.toInt().toString()
        }
    }

    override fun onResume() {
        super.onResume()
        if(stepSensor == null){
            accelerometerSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
        else{
            stepSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
        const val STEP_CALORIES_FACTOR = 0.035
    }
}