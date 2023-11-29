package com.example.gofitness.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gofitness.databinding.FragmentRegisterFormBinding
import com.example.gofitness.ui.AuthenticationNavigator
import com.google.firebase.auth.FirebaseAuth

class RegisterFormFragment : Fragment() {
    private lateinit var binding :FragmentRegisterFormBinding
    lateinit var authenticationNavigator : AuthenticationNavigator
    private lateinit var firebaseAuth : FirebaseAuth
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
        firebaseAuth = FirebaseAuth.getInstance()
        binding.btnGoBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnSubmit.setOnClickListener {
            val email = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if(email.isEmpty()){
                Toast.makeText(requireContext(),"Enter Email",Toast.LENGTH_SHORT).show()
            }
            if(password.isEmpty()){
                Toast.makeText(requireContext(),"Enter Password",Toast.LENGTH_SHORT).show()
            }
            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if(password==confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(requireContext(),"Successful Register",Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                        else{
                            Toast.makeText(requireContext(),"Fail Register",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    companion object{
        const val NAVIGATE_TO_LOGIN_FROM_REGISTER = "REGISTER_TO_LOGIN"
    }
}