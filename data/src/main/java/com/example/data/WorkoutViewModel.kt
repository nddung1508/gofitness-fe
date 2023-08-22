package com.example.data

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import entity.Exercise
import entity.Workout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class WorkoutViewModel(application: Application, private val context: Context) : ViewModel() {
    private val workoutDao = AppDatabase.getInstance(application).workoutDao()

    fun insertExercises(workout: Workout) {
        viewModelScope.launch {
            workoutDao.insertWorkout(workout)
        }
    }

    fun getAllWorkout(): LiveData<List<Workout>> {
        return workoutDao.getAllWorkouts()
    }
    fun insertDefaultExercises(){
        viewModelScope.launch(Dispatchers.IO) {
            val pushUp = R.drawable.push_up
            val pullUp = R.drawable.pull_up
            val armCircle = R.drawable.arm_circle
            val plankShoulderTap = R.drawable.plank_shoulder_tap
            val tricepsDip = R.drawable.tricep_dip
            val reverseSnowAngel = R.drawable.reverse_snow_angel
            val armExercise = listOf(
                Exercise(
                    name = "Push Up",
                    duration = 30,
                    caloriesBurned = 30.0,
                    definition = "A push-up is a bodyweight exercise in which you start in a plank position and use your arms to lower and lift your body, primarily working the chest, shoulders, and triceps muscles.",
                    goodFor = "Push-ups are a versatile bodyweight exercise that primarily target the muscles in your chest (pectoral muscles), shoulders (deltoids), and triceps. They also engage your core and stabilizing muscles.",
                    rep = 0,
                    image = ConvertResourceToByteArray(pushUp)
                ),
                Exercise(
                    name = "Pull Up",
                    duration = 45,
                    caloriesBurned = 100.0,
                    definition = "A pull-up is a bodyweight exercise where you hang from a bar with your palms facing away from you (overhand grip) and use your upper body muscles to pull your body up until your chin clears the bar. ",
                    goodFor = "This exercise primarily targets your back muscles (latissimus dorsi), biceps, and shoulders, as well as engaging your core and other supporting muscles. Pull-ups are effective for building upper body strength, particularly in your back and arms",
                    rep = 0,
                    image = ConvertResourceToByteArray(pullUp)
                ),
                Exercise(
                    name = "Arm Circle",
                    duration = 100,
                    caloriesBurned = 50.0,
                    definition = "Arm Circle is ...",
                    goodFor = "...",
                    rep = 0,
                    image = ConvertResourceToByteArray(armCircle)
                ),
                Exercise(
                    name = "Triceps Dip",
                    duration = 150,
                    caloriesBurned = 70.0,
                    definition = "Triceps Dip is ...",
                    goodFor = "...",
                    rep = 0,
                    image = ConvertResourceToByteArray(tricepsDip)
                ),
                Exercise(
                    name = "Plank Shoulder Tap",
                    duration = 50,
                    caloriesBurned = 35.0,
                    definition = "Plank Shoulder Tap is ...",
                    goodFor = "...",
                    rep = 0,
                    image = ConvertResourceToByteArray(plankShoulderTap)
                ),
                Exercise(
                    name = "Reverse Snow Angel",
                    duration = 60,
                    caloriesBurned = 100.0,
                    definition = "Reverse Snow Angel ...",
                    goodFor = "...",
                    rep = 0,
                    image = ConvertResourceToByteArray(reverseSnowAngel)
                )
            )
            val armWorkout = Workout(name = "Arm Workout", exercises = armExercise)
            workoutDao.insertWorkout(armWorkout)
        }
    }
    private fun ConvertResourceToByteArray(imageResource: Int): ByteArray {
        val imageBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, imageResource)
        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 1, stream)
        return stream.toByteArray()
    }
}