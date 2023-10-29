package history

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.home.databinding.LayoutRunningHistoryItemBinding
import entity.Running
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class RunningHistoryAdapter(private val context : Context) :  RecyclerView.Adapter<RunningHistoryAdapter.RunningHistoryViewHolder>() {
    var runningHistories : List<Running> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun addRunningHistories(data: List<Running>){
        runningHistories = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunningHistoryViewHolder {
        return RunningHistoryViewHolder(
            LayoutRunningHistoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount() : Int = runningHistories.size

    override fun onBindViewHolder(holder: RunningHistoryViewHolder, position: Int) {
        holder.bind(runningHistories[position])
    }

    inner class RunningHistoryViewHolder(private val binding: LayoutRunningHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        private val duration = binding.tvMin
        private val date = binding.tvDate
        private val kcal = binding.tvKcal
        private val distance = binding.tvDistance
        fun bind(value: Running) {
            val formattedDate = sdf.format(Date(value.dateInMillis))
            duration.text = updateDurationText(value.duration)
            date.text = formattedDate
            kcal.text = String.format("%.2f", value.kcal)
            val distanceInKilometer = value.distance/1000
            distance.text = String.format("%.2f", distanceInKilometer)
            binding.ivRunning.setOnClickListener {
                val intent = Intent(context, RunningHistoryMapActivity::class.java)
                val data = PolyLines(value.polylines)
                intent.putExtra("polyLines", data)
                context.startActivity(intent)
            }
        }
    }

    private fun updateDurationText(time: Int): String {
        val minutes = time / 60
        val seconds = time % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}