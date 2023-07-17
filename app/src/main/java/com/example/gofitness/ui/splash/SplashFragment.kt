package com.example.gofitness.ui.splash

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gofitness.databinding.FragmentSplashBinding
import com.example.gofitness.ui.AuthenticationNavigator
import com.example.gofitness.ui.MainActivity
import com.example.gofitness.ui.login.LoginFragment
import com.example.gofitness.ui.login.RegisterFormFragment

class SplashFragment  : Fragment(), AuthenticationNavigator {
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

    companion object {
        const val NAVIGATE_TO_LOGIN = "SPLASH_TO_LOGIN"
        const val NAVIGATE_TO_REGISTER = "LOGIN_TO_REGISTER_FORM"
        const val NAVIGATE_TO_LOGIN_FROM_REGISTER = "REGISTER_TO_LOGIN"
    }

    override fun navigateScreen(screenName: String, bundle: Bundle?) {
        when (screenName) {
            NAVIGATE_TO_LOGIN -> {
                val loginFragment =  LoginFragment()
                this.binding.root.isClickable = false
                loginFragment.authenticationNavigator = this
                (requireActivity() as MainActivity).addFragment(loginFragment)
            }
            NAVIGATE_TO_REGISTER -> {
                val registerFormFragment = RegisterFormFragment()
                this.binding.root.isClickable = false
                registerFormFragment.authenticationNavigator = this
                (requireActivity() as MainActivity).addFragment(RegisterFormFragment())
            }

            NAVIGATE_TO_LOGIN_FROM_REGISTER -> {
                val loginFragment =  LoginFragment()
                this.binding.root.isClickable = false
                loginFragment.authenticationNavigator = this
                (requireActivity() as MainActivity).navigateToFragment(loginFragment)
            }
        }
    }
}