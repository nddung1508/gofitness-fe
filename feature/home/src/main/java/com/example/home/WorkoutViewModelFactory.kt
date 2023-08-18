package com.example.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.WorkoutDao
import com.example.data.WorkoutViewModel

class WorkoutViewModelFactory(private val application: Application, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            return WorkoutViewModel(application, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}