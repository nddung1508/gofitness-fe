package com.example.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.WorkoutDao
import com.example.data.WorkoutViewModel

class WorkoutViewModelFactory(private val workoutDao: WorkoutDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            return WorkoutViewModel(workoutDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}