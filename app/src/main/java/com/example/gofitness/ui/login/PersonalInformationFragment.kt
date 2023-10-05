package com.example.gofitness.ui.login

import PersonalInformationViewModel
import android.R
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gofitness.databinding.FragmentPersonalInformationBinding
import com.example.gofitness.ui.AuthenticationNavigator
import com.example.gofitness.ui.MainActivity


class PersonalInformationFragment : Fragment() {
    private lateinit var binding : FragmentPersonalInformationBinding
    private lateinit var personalInformationViewModel : PersonalInformationViewModel
    lateinit var authenticationNavigator : AuthenticationNavigator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPersonalInformationBinding.inflate(layoutInflater)
        personalInformationViewModel = ViewModelProvider(this).get(PersonalInformationViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ArrayAdapter.createFromResource(requireActivity(), com.example.gofitness.R.array.goals, R.layout.simple_spinner_item)
        binding.spGoal.adapter = adapter
        binding.btnUpdate.isEnabled = (binding.etHeight.text.toString().isNotEmpty() && binding.etWeight.text.toString().isNotEmpty() && binding.spGoal.isNotEmpty())
        binding.etHeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.btnUpdate.isEnabled = (binding.etHeight.text.toString().isNotEmpty() && binding.etWeight.text.toString().isNotEmpty() && binding.spGoal.isNotEmpty())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.etWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.btnUpdate.isEnabled = (binding.etHeight.text.toString().isNotEmpty() && binding.etWeight.text.toString().isNotEmpty() && binding.spGoal.isNotEmpty())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

            binding.btnUpdate.setOnClickListener {
            personalInformationViewModel.addPersonalInformation(height = binding.etHeight.text.toString().toInt(),
                weight= binding.etWeight.text.toString().toInt(), goal = binding.spGoal.selectedItem.toString())
            personalInformationViewModel.getPersonalInformation().observe(viewLifecycleOwner){
                if(it != null)    {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}