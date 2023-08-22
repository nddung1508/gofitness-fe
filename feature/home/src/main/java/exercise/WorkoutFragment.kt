package exercise

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.data.WorkoutViewModel
import com.example.home.ExerciseNavigator
import com.example.home.WorkoutViewModelFactory
import com.example.home.databinding.FragmentWorkoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkoutFragment : Fragment() {
    private lateinit var binding: FragmentWorkoutBinding
    private lateinit var workoutViewModel: WorkoutViewModel
    lateinit var exerciseNavigator: ExerciseNavigator
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workoutViewModel = ViewModelProvider(this, WorkoutViewModelFactory(requireActivity().application, requireContext()))[WorkoutViewModel::class.java]
        val workoutAdapter = WorkoutAdapter(exerciseNavigator)
        binding.rvExercise.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        binding.rvExercise.adapter = workoutAdapter
        coroutineScope.launch {
            workoutViewModel.getAllWorkout().observe(viewLifecycleOwner, Observer { workouts ->
                workoutAdapter.addExercises(workouts[0].exercises)
            })
        }
    }
}