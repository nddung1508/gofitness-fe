package com.example.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.home.databinding.FragmentExerciseTypeBinding
import exercise.WorkoutFragment

class ExerciseTypeFragment :Fragment(), ExerciseNavigator{
    private lateinit var binding: FragmentExerciseTypeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseTypeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }
        binding.clGymPlan.setOnClickListener {
            navigateScreen(NAVIGATE_TO_GYM_PLAN)
        }
        binding.clHomePlan.setOnClickListener {
            navigateScreen(NAVIGATE_TO_HOME_PLAN)
        }
    }

    override fun navigateScreen(screenName: String, bundle: Bundle?) {
        when (screenName) {
            NAVIGATE_TO_HOME_PLAN -> {
                val homePlanFragment = HomePlanFragment()
                this.binding.root.isClickable = false
                homePlanFragment.exerciseNavigator = this
                (requireActivity() as ExerciseActivity).addFragment(homePlanFragment)
            }
            NAVIGATE_TO_GYM_PLAN -> {
                val gymPlanFragment = GymPlanFragment()
                this.binding.root.isClickable = false
                gymPlanFragment.exerciseNavigator = this
                (requireActivity() as ExerciseActivity).addFragment(gymPlanFragment)
            }
            NAVIGATE_TO_WORKOUT -> {
                val workoutFragment = WorkoutFragment()
                this.binding.root.isClickable = false
                (requireActivity() as ExerciseActivity).addFragment(workoutFragment)
            }
        }
    }
    companion object{
        const val NAVIGATE_TO_HOME_PLAN = "EXERCISE_TYPE_TO_HOME_PLAN"
        const val NAVIGATE_TO_GYM_PLAN = "EXERCISE_TYPE_TO_GYM_PLAN"
        const val NAVIGATE_TO_WORKOUT = "PLAN_TO_WORKOUT"
    }
}