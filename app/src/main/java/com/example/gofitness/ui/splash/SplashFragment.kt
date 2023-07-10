package com.example.gofitness.ui.splash

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gofitness.databinding.FragmentSplashBinding
import com.example.gofitness.ui.MainActivity
import com.example.gofitness.ui.login.LoginFragment

class SplashFragment  : Fragment() {
private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        handleNextScreen()
        return binding.root
    }
    private fun handleNextScreen(){
        navigateScreen(NAVIGATE_TO_LOGIN)
    }

    fun navigateScreen(screenName: String) {
        when (screenName) {
            NAVIGATE_TO_LOGIN -> {
                val loginFragment =  LoginFragment()
                this.binding.root.isClickable = false
                (requireActivity() as MainActivity).addFragment(loginFragment)
            }
        }
    }

    companion object {
        const val NAVIGATE_TO_LOGIN = "SPLASH_TO_LOGIN"
    }

}