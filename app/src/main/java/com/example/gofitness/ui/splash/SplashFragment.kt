package com.example.gofitness.ui.splash

import PersonalInformationViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gofitness.databinding.FragmentSplashBinding
import com.example.gofitness.ui.AuthenticationNavigator
import com.example.gofitness.ui.MainActivity
import com.example.gofitness.ui.login.LoginFormFragment
import com.example.gofitness.ui.login.LoginFragment
import com.example.gofitness.ui.login.PersonalInformationFragment
import com.example.gofitness.ui.login.RegisterFormFragment
import com.example.gofitness.ui.main.MainFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashFragment  : Fragment(), AuthenticationNavigator {
private lateinit var binding: FragmentSplashBinding
private lateinit var firebaseAuth : FirebaseAuth
private lateinit var personalInformationViewModel : PersonalInformationViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        personalInformationViewModel = ViewModelProvider(this).get(PersonalInformationViewModel::class.java)
        handleNextScreen()
        return binding.root
    }
    private fun handleNextScreen(){
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { Log.d("UID", "UserID: " + it.uid) }
        if (currentUser == null){
            navigateScreen(NAVIGATE_TO_LOGIN)
        }
        else{
            personalInformationViewModel.getPersonalInformation().observe(viewLifecycleOwner){
                if (currentUser != null && it != null){
                    navigateScreen(NAVIGATE_TO_MAIN)
                }
                else if(currentUser != null){
                    FirebaseAuth.getInstance().signOut()
                    navigateScreen(NAVIGATE_TO_LOGIN)
                }
            }
        }
    }
    override fun navigateScreen(screenName: String, bundle: Bundle?) {
        when (screenName) {
            NAVIGATE_TO_LOGIN -> {
                val loginFragment = LoginFragment()
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
                val loginFragment = LoginFragment()
                this.binding.root.isClickable = false
                loginFragment.authenticationNavigator = this
                (requireActivity() as MainActivity).navigateToFragment(loginFragment)
            }

            NAVIGATE_TO_LOGIN_FORM -> {
                val loginFormFragment = LoginFormFragment()
                this.binding.root.isClickable = false
//                loginFormFragment.authenticationNavigator = this
                (requireActivity() as MainActivity).addFragment(loginFormFragment)
            }

            NAVIGATE_TO_MAIN -> {
                val mainFragment = MainFragment()
                this.binding.root.isClickable = false
                (requireActivity() as MainActivity).navigateToFragment(mainFragment)
            }
            NAVIGATE_TO_USER_INFORMATION ->{
                val personalInformationFragment = PersonalInformationFragment()
                this.binding.root.isClickable = false
                personalInformationFragment.authenticationNavigator = this
                (requireActivity() as MainActivity).navigateToFragment(personalInformationFragment)
            }
            LOG_OUT -> {
                val splashFragment = SplashFragment()
                (requireActivity() as MainActivity).navigateToFragment(splashFragment)
            }
        }
    }

    companion object {
        const val NAVIGATE_TO_LOGIN = "SPLASH_TO_LOGIN"
        const val NAVIGATE_TO_REGISTER = "LOGIN_TO_REGISTER_FORM"
        const val NAVIGATE_TO_LOGIN_FROM_REGISTER = "REGISTER_TO_LOGIN"
        const val NAVIGATE_TO_LOGIN_FORM = "LOGIN_TO_LOGIN_FORM"
        const val NAVIGATE_TO_MAIN = "SPLASH_TO_MAIN"
        const val NAVIGATE_TO_USER_INFORMATION = "LOGIN_TO_USER_INFORMATION"
        const val LOG_OUT = "MAIN_TO_LOGIN"
    }
}