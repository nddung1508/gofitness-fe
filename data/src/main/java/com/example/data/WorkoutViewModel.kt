package com.example.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import entity.Exercise
import entity.Workout
import kotlinx.coroutines.launch

class WorkoutViewModel(private val workoutDao: WorkoutDao) : ViewModel() {
    fun insertExercises(workout: Workout) {
        viewModelScope.launch {
            workoutDao.insertWorkout(workout)
        }
    }

    fun getAllWorkout(): LiveData<List<Workout>> {
        return workoutDao.getAllWorkouts()
    }
}