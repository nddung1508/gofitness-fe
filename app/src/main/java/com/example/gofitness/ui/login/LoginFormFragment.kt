package com.example.gofitness.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gofitness.databinding.FragmentLoginFormBinding
import com.example.gofitness.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth

class LoginFormFragment : Fragment() {
    private lateinit var binding :FragmentLoginFormBinding
    private lateinit var firebaseAuth : FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginFormBinding.inflate(inflater, container, false)
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
            if(email.isEmpty()){
                Toast.makeText(requireContext(),"Enter Email",Toast.LENGTH_SHORT).show()
            }
            if(password.isEmpty()){
                Toast.makeText(requireContext(),"Enter Password",Toast.LENGTH_SHORT).show()
            }
            if(email.isNotEmpty() && password.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(requireContext(),"Successful Login",Toast.LENGTH_SHORT).show()
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(requireContext(),"Fail Login",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }