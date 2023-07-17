package com.example.gofitness.ui

import android.os.Bundle

interface AuthenticationNavigator {
    fun navigateScreen(screenName:String, bundle: Bundle? = null)
}