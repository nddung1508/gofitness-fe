package exercise

import KcalByDayViewModel
import WorkoutHistoryViewModel
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.home.databinding.FragmentStartWorkoutBinding
import entity.KcalByDay
import entity.Workout
import java.io.ByteArrayOutputStream

class StartWorkoutFragment : Fragment() {
    private lateinit var binding: FragmentStartWorkoutBinding
    private var isShowingDialog = false
    private var cancelRestTimer = false
    private lateinit var workoutHistoryViewModel: WorkoutHistoryViewModel
    private lateinit var kcalByDayViewModel: KcalByDayViewModel
    private lateinit var startExerciseTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        workoutHistoryViewModel = ViewModelProvider(this).get(WorkoutHistoryViewModel::class.java)
        binding = FragmentStartWorkoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val receivedExercise = arguments?.getParcelable<Workout>("workout")
        val startIndex = 0
        if (receivedExercise != null) {
            getExercise(receivedExercise, startIndex)
        }
    }

    private fun getExercise(receivedExercise: Workout, startIndex: Int) {
        val startExerciseDuration = receivedExercise.exercises[startIndex].duration.toLong()

        binding.clRest.visibility = View.GONE
        binding.clStartExercise.visibility = View.VISIBLE
        binding.ivCurrentExercise.setImageBitmap(receivedExercise.exercises[startIndex].image?.let {
            decodeByteArray(
                it
            )
        })
        binding.tvExerciseName.text = receivedExercise.exercises[startIndex].name
        startExerciseTimer = object : CountDownTimer(startExerciseDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvExerciseDuration.text = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                val nextIndex = startIndex + 1
                if (nextIndex < receivedExercise.exercises.size) {
                    takeRest(receivedExercise, nextIndex)
                    this.cancel()
                } else {
                    if (!isShowingDialog) {
                        isShowingDialog = true
                        addWorkoutHistory(receivedExercise)
                        addKcalByDay(
                            KcalByDay(
                                receivedExercise.exercises.sumOf { it.caloriesBurned },
                                System.currentTimeMillis()
                            )
                        )
                        finishPopUp(receivedExercise)
                        this.cancel()
                    }
                }
            }
        }
        startExerciseTimer.start()
    }

    private fun takeRest(receivedExercise: Workout, nextIndex: Int) {
        cancelRestTimer = false
        val restExerciseTimer = object : CountDownTimer(10000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.tvRestDuration.text = secondsRemaining.toString()
                binding.btnFinish.setOnClickListener {
                    cancelRestTimer = true
                    getExercise(receivedExercise, nextIndex)
                    cancel()
                }
            }

            override fun onFinish() {
                if (!cancelRestTimer) {
                    cancelRestTimer = false
                    getExercise(receivedExercise, nextIndex)
                    cancel()
                }
            }
        }

        binding.clStartExercise.visibility = View.GONE
        binding.clRest.visibility = View.VISIBLE
        binding.tvNextExercise.text = receivedExercise.exercises[nextIndex].name
        binding.tvNextNumber.text = "Next: ${nextIndex + 1}/${receivedExercise.exercises.size}"
        binding.ivRestExercise.setImageBitmap(receivedExercise.exercises[nextIndex].image?.let {
            decodeByteArray(
                it
            )
        })
        restExerciseTimer.start()
    }

    private fun addWorkoutHistory(receivedExercise: Workout) {
        val pushUp = com.example.data.R.drawable.push_up
        receivedExercise.let {
            workoutHistoryViewModel.addWorkoutHistory(
                name = it.name,
                duration = it.exercises.sumOf { it.duration }.toLong(),
                caloriesBurned = it.exercises.sumOf { it.caloriesBurned }.toInt(),
                image = convertResourceToByteList(requireContext(), pushUp),
                amountOfExercise = it.exercises.size,
                currentTime = System.currentTimeMillis()
            )
        }
    }

    private fun addKcalByDay(kcalByDay: KcalByDay) {
        kcalByDayViewModel = ViewModelProvider(this).get(KcalByDayViewModel::class.java)
        kcalByDayViewModel.addKcalData(kcalByDay.kcal)
    }

    private fun finishPopUp(workout: Workout) {
        AlertDialog.Builder(this.requireContext()).run {
            setTitle(
                workout.name.lowercase()
                    .replaceFirstChar { firstChar -> firstChar.uppercase() } + " Exercise")
            setMessage("You have finish the exercise.")
            setPositiveButton("OK") { dialog, _ ->
                requireActivity().supportFragmentManager.popBackStack()
                dialog.dismiss()
                isShowingDialog = false
            }
            create()
        }.show()
    }

    private fun formatTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun decodeByteArray(imageByteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }

    private fun convertResourceToByteList(context: Context, resourceId: Int): List<Byte> {
        val imageBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 1, stream)
        val byteArray = stream.toByteArray()
        return byteArray.asList()
    }
}