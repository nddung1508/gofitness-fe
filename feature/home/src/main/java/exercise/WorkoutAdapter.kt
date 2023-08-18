package exercise

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.home.databinding.LayoutWorkoutExerciseItemBinding
import entity.Exercise

class WorkoutAdapter : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {
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
        private val rep = binding.tvRep
        fun bind(value: Exercise) {
            image.setImageBitmap(value.image?.let { decodeByteArray(it) })
            name.text = value.name
            duration.text = value.duration.toString()
            rep.text = value.rep.toString()
        }
    }

    private fun decodeByteArray(imageByteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }
}
