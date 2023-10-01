package com.example.gofitness.ui.login

import PersonalInformationViewModel
import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gofitness.databinding.FragmentPersonalInformationBinding


class PersonalInformationFragment : Fragment() {
    private lateinit var binding : FragmentPersonalInformationBinding
    private lateinit var personalInformationViewModel : PersonalInformationViewModel
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
        binding.btnUpdate.setOnClickListener {
            personalInformationViewModel.addPersonalInformation(height = binding.etHeight.text.toString().toInt(),
                weight= binding.etWeight.text.toString().toInt(), goal = binding.spGoal.selectedItem.toString())
        }
    }
}