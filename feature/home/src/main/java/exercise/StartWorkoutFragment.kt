package exercise

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.home.databinding.FragmentStartWorkoutBinding
import entity.Exercise
import entity.Workout
import java.io.ByteArrayOutputStream

class StartWorkoutFragment : Fragment() {
    private lateinit var binding: FragmentStartWorkoutBinding
    private lateinit var countdownTimer: CountDownTimer
    private var countdownDurationMillis : Long = 0L

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
        countdownDurationMillis = 0L
        if (receivedExercise != null) {
            getExercise(receivedExercise, startIndex)
        }
        //Countdown (Exercise Duration)

        countdownTimer = object : CountDownTimer(countdownDurationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.tvExerciseDuration.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                startIndex++
                if (receivedExercise != null) {
                    getExercise(receivedExercise, startIndex)
                }
            }
        }
        countdownTimer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer.cancel()
    }

    private fun getExercise(receivedExercise : Workout, startIndex : Int){
            binding.ivCurrentExercise.setImageBitmap(receivedExercise.exercises[startIndex].image?.let {
                decodeByteArray(
                    it
                )
            })
        countdownDurationMillis = receivedExercise.exercises[startIndex].duration.toLong()
    }

    private fun decodeByteArray(imageByteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }
}