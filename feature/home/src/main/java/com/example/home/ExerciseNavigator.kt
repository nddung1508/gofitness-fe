package com.example.home

import android.os.Bundle

interface ExerciseNavigator {
    fun navigateScreen(screenName:String, bundle: Bundle? = null)
}