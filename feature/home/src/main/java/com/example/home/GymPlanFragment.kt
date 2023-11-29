package com.example.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.home.databinding.FragmentGymPlanBinding

class GymPlanFragment : Fragment() {
    lateinit var exerciseNavigator : ExerciseNavigator
    private lateinit var binding : FragmentGymPlanBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGymPlanBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
         binding.clAbs.setOnClickListener {
            val bundle = Bundle().apply {
                putString("type", "ABS")
            }
            exerciseNavigator.navigateScreen(NAVIGATE_TO_WORKOUT, bundle)
        }
        binding.clArm.setOnClickListener {
            val bundle = Bundle().apply {
                putString("type", "ARM")
            }
            exerciseNavigator.navigateScreen(NAVIGATE_TO_WORKOUT, bundle)
        }
        binding.clLegs.setOnClickListener {
            val bundle = Bundle().apply {
                putString("type", "LEGS")
            }
            exerciseNavigator.navigateScreen(NAVIGATE_TO_WORKOUT, bundle)
        }
        binding.clChest.setOnClickListener {
            val bundle = Bundle().apply {
                putString("type", "CHEST")
            }
            exerciseNavigator.navigateScreen(NAVIGATE_TO_WORKOUT, bundle)
        }
        binding.clShoulder.setOnClickListener {
            val bundle = Bundle().apply {
                putString("type", "SHOULDER")
            }
            exerciseNavigator.navigateScreen(NAVIGATE_TO_WORKOUT, bundle)
        }
    }
    companion object{
                const val NAVIGATE_TO_WORKOUT = "PLAN_TO_WORKOUT"
    }
}