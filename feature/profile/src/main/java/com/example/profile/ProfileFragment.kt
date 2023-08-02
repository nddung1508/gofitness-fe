package com.example.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.profile.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    lateinit var profileNavigator : ProfileNavigator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogout.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if(currentUser != null){
                FirebaseAuth.getInstance().signOut()
                Thread.sleep(3000)
                profileNavigator.navigateScreen()
            }
        }
    }
    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}