package com.example.gofitness.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gofitness.databinding.FragmentMainBinding
import com.example.gofitness.ui.AuthenticationNavigator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class MainFragment : Fragment() {
    private lateinit var binding : FragmentMainBinding
    lateinit var authenticationNavigator: AuthenticationNavigator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogout.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if(currentUser != null){
                FirebaseAuth.getInstance().signOut()
                Thread.sleep(1500)
                authenticationNavigator.navigateScreen(LOG_OUT)
            }
        }
    }
    companion object{
        const val LOG_OUT = "MAIN_TO_LOGIN"
    }
}