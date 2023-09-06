package history

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.home.databinding.LayoutWorkoutExerciseItemBinding
import com.example.home.databinding.LayoutWorkoutHistoryItemBinding
import entity.Exercise
import entity.WorkoutHistory
import java.io.ByteArrayInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutHistoryAdapter :  RecyclerView.Adapter<WorkoutHistoryAdapter.WorkoutHistoryViewHolder>() {
    var workoutHistories : List<WorkoutHistory> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun addWorkoutHistory(data: List<WorkoutHistory>){
        workoutHistories = data
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutHistoryViewHolder {
        return WorkoutHistoryViewHolder(LayoutWorkoutHistoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount() : Int = workoutHistories.size

    override fun onBindViewHolder(holder: WorkoutHistoryViewHolder, position: Int) {
        holder.bind(workoutHistories[position])
    }

    inner class WorkoutHistoryViewHolder(private val binding: LayoutWorkoutHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

        private val image = binding.ivWorkout
        private val name = binding.tvWorkoutName
        private val duration = binding.tvMin
        private val date = binding.tvDate
        private val kcal = binding.tvKcal
        private val exercise = binding.tvExercises
        fun bind(value: WorkoutHistory) {
            val formattedDate = sdf.format(Date(value.currentTime))
            image.setImageBitmap(decodeBase64ToBitmap(value.image))
            name.text = value.name
            duration.text = value.duration.toString()
            date.text = formattedDate
            kcal.text = value.caloriesBurned.toString()
            exercise.text = value.amountOfExercise.toString()
            }
        }

    private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val inputStream = ByteArrayInputStream(decodedBytes)
        return BitmapFactory.decodeStream(inputStream)
    }
}