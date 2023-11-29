package exercise

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.home.ExerciseNavigator
import com.example.home.databinding.LayoutWorkoutExerciseItemBinding
import entity.Exercise

class WorkoutAdapter(val exerciseNavigator: ExerciseNavigator) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {
    var exercises: List<Exercise> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun addExercises(data: List<Exercise>){
        exercises = data
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        return WorkoutViewHolder( LayoutWorkoutExerciseItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))

    }

    override fun getItemCount(): Int = exercises.size

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    inner class WorkoutViewHolder(private val binding: LayoutWorkoutExerciseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val image = binding.ivWorkout
        val name = binding.tvExerciseName
        private val duration = binding.tvExerciseDuration
//        private val rep = binding.tvRep
        private val detail = binding.btnQuestionMark
        fun bind(value: Exercise) {
            image.setImageBitmap(value.image?.let { decodeByteArray(it) })
            name.text = value.name
            duration.text = formatTime(value.duration.toLong())
//            rep.text = value.rep.toString()
            detail.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("detail", value.name)
                    putString("definition", value.definition)
                    putString("about", value.goodFor)
                    putByteArray("image", value.image)
                }
                exerciseNavigator.navigateScreen(NAVIGATE_TO_WORKOUT_DETAIL, bundle)
            }
        }
    }

    private fun decodeByteArray(imageByteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }

    private fun formatTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    companion object{
        const val NAVIGATE_TO_WORKOUT_DETAIL = "WORKOUT_TO_WORKOUT_DETAIL"
    }
}
