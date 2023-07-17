package com.example.gofitness.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gofitness.databinding.FragmentRegisterFormBinding
import com.example.gofitness.ui.AuthenticationNavigator
import com.example.gofitness.ui.MainActivity

class RegisterFormFragment : Fragment() {
    private lateinit var binding :FragmentRegisterFormBinding
    lateinit var authenticationNavigator : AuthenticationNavigator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGoBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    companion object{
        const val NAVIGATE_TO_LOGIN_FROM_REGISTER = "REGISTER_TO_LOGIN"
    }
}