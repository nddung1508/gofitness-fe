package com.example.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.WorkoutViewModel
import com.example.home.databinding.FragmentHomePlanBinding
import exercise.WorkoutAdapter
import kotlinx.coroutines.delay
import kotlin.concurrent.thread

class HomePlanFragment : Fragment() {
    lateinit var exerciseNavigator : ExerciseNavigator
    private lateinit var binding : FragmentHomePlanBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomePlanBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.clAbs.setOnClickListener {
            exerciseNavigator.navigateScreen(NAVIGATE_TO_WORKOUT)
        }
    }

    companion object{
        const val NAVIGATE_TO_WORKOUT = "PLAN_TO_WORKOUT"
    }
}