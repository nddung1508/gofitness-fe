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
import com.example.data.ExerciseViewModel
import com.example.data.ExerciseViewModelFactory
import com.example.home.databinding.FragmentHomePlanBinding

class HomePlanFragment : Fragment() {
    lateinit var exerciseNavigator : ExerciseNavigator
    private lateinit var binding : FragmentHomePlanBinding
    private lateinit var exerciseViewModel: ExerciseViewModel
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
        val appDatabase = AppDatabase.getInstance(requireContext())
        val exerciseDao = appDatabase.exerciseDao()

        val viewModelFactory = ExerciseViewModelFactory(exerciseDao)
        exerciseViewModel = ViewModelProvider(this, viewModelFactory)[ExerciseViewModel::class.java]
        exerciseViewModel.getAllExercises().observe(viewLifecycleOwner, Observer { exercises ->
            Toast.makeText(requireContext(), exercises[0].name,Toast.LENGTH_LONG).show()
        })
    }
}