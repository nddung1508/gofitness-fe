package exercise

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.home.databinding.FragmentWorkoutDetailBinding

class WorkoutDetailFragment : Fragment() {
    private lateinit var binding: FragmentWorkoutDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkoutDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val exerciseName = arguments?.getString("detail")
        val image = arguments?.getByteArray("image")
        val definition = arguments?.getString("definition")
        val about = arguments?.getString("about")
        binding.tvExercise.text = exerciseName
        binding.ivWorkout.setImageBitmap(image?.let { decodeByteArray(it) })
        binding.tvDefinition.text = definition
        binding.tvWhatFor.text = about

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun decodeByteArray(imageByteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }
}