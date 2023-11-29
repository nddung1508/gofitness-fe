package exercise

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.data.R
import com.example.home.ExerciseNavigator
import com.example.home.databinding.FragmentWorkoutBinding
import entity.Exercise
import entity.Workout
import java.io.ByteArrayOutputStream

class WorkoutFragment : Fragment() {
    private lateinit var binding: FragmentWorkoutBinding
    lateinit var exerciseNavigator: ExerciseNavigator
    private var workout : Workout ? = null

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
        val type = arguments?.getString("type")
        type?.let { getAllWorkout(it) }
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        val workoutAdapter = WorkoutAdapter(exerciseNavigator)
        binding.rvExercise.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        workout?.let { workoutAdapter.addExercises(it.exercises) }
        binding.rvExercise.adapter = workoutAdapter
        val bundle = Bundle().apply {
            putParcelable("workout", workout)
        }
        binding.btnStartWorkout.setOnClickListener {
            exerciseNavigator.navigateScreen(NAVIGATE_TO_START_WORKOUT, bundle)
        }
    }

    private fun getAllWorkout(selectedExercise : String){
        val pushUp = R.drawable.push_up
        val pullUp = R.drawable.pull_up
        val armCircle = R.drawable.arm_circle
        val plankShoulderTap = R.drawable.plank_shoulder_tap
        val tricepsDip = R.drawable.tricep_dip
        val reverseSnowAngel = R.drawable.reverse_snow_angel
        var exercise = listOf<Exercise>()
        when(selectedExercise){
            "ABS" -> {
                exercise = listOf(
                    Exercise(
                        name = "Push Up", duration = 10000, caloriesBurned = 30.0, definition = "A push-up is a bodyweight exercise in which you start in a plank position and use your arms to lower and lift your body, primarily working the chest, shoulders, and triceps muscles.", goodFor = "Push-ups are a versatile bodyweight exercise that primarily target the muscles in your chest (pectoral muscles), shoulders (deltoids), and triceps. They also engage your core and stabilizing muscles.", rep = 0,
                        image = ConvertResourceToByteArray(pushUp)
                    ), Exercise(
                        name = "Pull Up", duration = 10000, caloriesBurned = 100.0, definition = "A pull-up is a bodyweight exercise where you hang from a bar with your palms facing away from you (overhand grip) and use your upper body muscles to pull your body up until your chin clears the bar. ", goodFor = "This exercise primarily targets your back muscles (latissimus dorsi), biceps, and shoulders, as well as engaging your core and other supporting muscles. Pull-ups are effective for building upper body strength, particularly in your back and arms", rep = 0,
                        image = ConvertResourceToByteArray(pullUp)
                    ), Exercise(
                        name = "Arm Circle", duration = 5000, caloriesBurned = 50.0, definition = "Arm Circle is ...", goodFor = "...", rep = 0,
                        image = ConvertResourceToByteArray(armCircle)
                    )
                )
            }
            "ARM" -> {
                exercise =  listOf(
                    Exercise(
                        name = "Triceps Dip", duration = 5000, caloriesBurned = 70.0, definition = "Triceps Dip is ...", goodFor = "...", rep = 0,
                        image = ConvertResourceToByteArray(tricepsDip)
                    ), Exercise(name = "Plank Shoulder Tap", duration = 5000, caloriesBurned = 35.0, definition = "Plank Shoulder Tap is ...", goodFor = "...", rep = 0,
                        image = ConvertResourceToByteArray(plankShoulderTap)
                    ), Exercise(
                        name = "Reverse Snow Angel", duration = 6000, caloriesBurned = 100.0, definition = "Reverse Snow Angel ...", goodFor = "...", rep = 0,
                        image = ConvertResourceToByteArray(reverseSnowAngel)
                        )
                    )
            }
            "CHEST" -> {
                 exercise =  listOf(
                    Exercise(
                        name = "Push Up", duration = 10000, caloriesBurned = 30.0, definition = "A push-up is a bodyweight exercise in which you start in a plank position and use your arms to lower and lift your body, primarily working the chest, shoulders, and triceps muscles.", goodFor = "Push-ups are a versatile bodyweight exercise that primarily target the muscles in your chest (pectoral muscles), shoulders (deltoids), and triceps. They also engage your core and stabilizing muscles.", rep = 0,
                        image = ConvertResourceToByteArray(pushUp)
                    ),  Exercise(name = "Plank Shoulder Tap", duration = 5000, caloriesBurned = 35.0, definition = "Plank Shoulder Tap is ...", goodFor = "...", rep = 0,
                        image = ConvertResourceToByteArray(plankShoulderTap)
                    ), Exercise(
                        name = "Reverse Snow Angel", duration = 20000, caloriesBurned = 100.0, definition = "Reverse Snow Angel ...", goodFor = "...", rep = 0,
                        image = ConvertResourceToByteArray(reverseSnowAngel)
                        )
                    )
            }
            "SHOULDER" -> {
                exercise = listOf(
                    Exercise(
                        name = "Push Up", duration = 3000, caloriesBurned = 30.0, definition = "A push-up is a bodyweight exercise in which you start in a plank position and use your arms to lower and lift your body, primarily working the chest, shoulders, and triceps muscles.", goodFor = "Push-ups are a versatile bodyweight exercise that primarily target the muscles in your chest (pectoral muscles), shoulders (deltoids), and triceps. They also engage your core and stabilizing muscles.", rep = 0,
                        image = ConvertResourceToByteArray(pushUp)
                    ), Exercise(
                        name = "Triceps Dip", duration = 5000, caloriesBurned = 70.0, definition = "Triceps Dip is ...", goodFor = "...", rep = 0,
                        image = ConvertResourceToByteArray(tricepsDip)
                    ),Exercise(name = "Plank Shoulder Tap", duration = 5000, caloriesBurned = 35.0, definition = "Plank Shoulder Tap is ...", goodFor = "...", rep = 0,
                        image = ConvertResourceToByteArray(plankShoulderTap)
                    )
                )
            }
            "LEGS" -> {
                exercise = listOf(
                    Exercise(
                        name = "Push Up", duration = 3000, caloriesBurned = 30.0, definition = "A push-up is a bodyweight exercise in which you start in a plank position and use your arms to lower and lift your body, primarily working the chest, shoulders, and triceps muscles.", goodFor = "Push-ups are a versatile bodyweight exercise that primarily target the muscles in your chest (pectoral muscles), shoulders (deltoids), and triceps. They also engage your core and stabilizing muscles.", rep = 0,
                        image = ConvertResourceToByteArray(pushUp)
                    ), Exercise(
                        name = "Pull Up", duration = 4500, caloriesBurned = 100.0, definition = "A pull-up is a bodyweight exercise where you hang from a bar with your palms facing away from you (overhand grip) and use your upper body muscles to pull your body up until your chin clears the bar. ", goodFor = "This exercise primarily targets your back muscles (latissimus dorsi), biceps, and shoulders, as well as engaging your core and other supporting muscles. Pull-ups are effective for building upper body strength, particularly in your back and arms", rep = 0,
                        image = ConvertResourceToByteArray(pullUp)
                    ), Exercise(
                        name = "Arm Circle", duration = 2000, caloriesBurned = 50.0, definition = "Arm Circle is ...", goodFor = "...", rep = 0,
                        image = ConvertResourceToByteArray(armCircle)
                    )
                )
            }
        }

        workout = Workout(name = selectedExercise, exercises = exercise)
    }

    private fun ConvertResourceToByteArray(imageResource: Int): ByteArray {
        val imageBitmap: Bitmap = BitmapFactory.decodeResource(requireContext().resources, imageResource)
        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 1, stream)
        return stream.toByteArray()
    }
    companion object{
        const val NAVIGATE_TO_START_WORKOUT = "WORKOUT_TO_START_WORKOUT"
    }
}