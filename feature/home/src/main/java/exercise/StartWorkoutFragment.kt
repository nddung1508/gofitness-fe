package exercise

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.home.ExerciseNavigator
import com.example.home.databinding.FragmentStartWorkoutBinding
import entity.Exercise
import entity.Workout
import java.io.ByteArrayOutputStream

class StartWorkoutFragment : Fragment() {
    private lateinit var binding: FragmentStartWorkoutBinding
    private lateinit var startExerciseTimer: CountDownTimer
    private lateinit var restExerciseTimer: CountDownTimer
    private var startExerciseDuration : Long = 0L
    private var isShowingDialog = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartWorkoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val receivedExercise = arguments?.getParcelable<Workout>("workout")
        var startIndex = 0
        startExerciseDuration = 0L
        if (receivedExercise != null) {
            getExercise(receivedExercise, startIndex)
        }

        startExerciseTimer = object : CountDownTimer(startExerciseDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvExerciseDuration.text = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                startIndex++
                if (receivedExercise != null) {
                    takeRest(receivedExercise, startIndex)
                }
            }
        }
        startExerciseTimer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        startExerciseTimer.cancel()
        restExerciseTimer.cancel()
    }

    private fun getExercise(receivedExercise : Workout, startIndex : Int){
        startExerciseDuration = receivedExercise.exercises[startIndex].duration.toLong()

        binding.clRest.visibility = View.GONE
        binding.clStartExercise.visibility = View.VISIBLE
        binding.ivCurrentExercise.setImageBitmap(receivedExercise.exercises[startIndex].image?.let {
                decodeByteArray(
                    it
                )
            })

        startExerciseTimer = object : CountDownTimer(startExerciseDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvExerciseDuration.text = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                val nextIndex = startIndex + 1
                if(nextIndex < receivedExercise.exercises.size){
                    takeRest(receivedExercise, nextIndex)
                }
                else{
                    if (!isShowingDialog) {
                        isShowingDialog = true
                        finishPopUp()
                    }
                }
            }
        }
        startExerciseTimer.start()
    }

    private fun takeRest(receivedExercise : Workout, nextIndex : Int){
        binding.clStartExercise.visibility = View.GONE
        binding.clRest.visibility = View.VISIBLE
        binding.tvNextExercise.text = receivedExercise.exercises[nextIndex].name
        binding.tvNextNumber.text = "Next: ${nextIndex+1}/${receivedExercise.exercises.size}"
        binding.ivRestExercise.setImageBitmap(receivedExercise.exercises[nextIndex].image?.let{
                decodeByteArray(
                    it
                )
            })
            restExerciseTimer = object : CountDownTimer(10000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = millisUntilFinished / 1000
                    binding.tvRestDuration.text = secondsRemaining.toString()
                    binding.btnFinish.setOnClickListener {
                        restExerciseTimer.onFinish()
                    }
                }

                override fun onFinish() {
                    getExercise(receivedExercise, nextIndex)
                }
            }
        restExerciseTimer.start()
    }

    private fun finishPopUp(){
        AlertDialog.Builder(this.requireContext()).run {
            setTitle("Alert Dialog")
            setMessage("You have finish the exercise.")
            setPositiveButton("OK") { dialog, _ ->
                requireActivity().supportFragmentManager.popBackStack()
                dialog.dismiss()
                isShowingDialog = false
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
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
}