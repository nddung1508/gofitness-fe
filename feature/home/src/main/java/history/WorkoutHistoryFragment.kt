package history

import WorkoutHistoryViewModel
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.home.databinding.FragmentWorkoutHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import entity.WorkoutHistory
import exercise.WorkoutAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WorkoutHistoryFragment : Fragment() {
    private lateinit var binding: FragmentWorkoutHistoryBinding
    private val calendar = Calendar.getInstance()
    private lateinit var workoutHistoryViewModel: WorkoutHistoryViewModel
    private lateinit var firebaseAuth : FirebaseAuth
    private var todayStartInMillis: Long = 0
    private var todayEndInMillis: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkoutHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.currentDate.setOnClickListener {
            showDatePickerDialog()
        }
        //Default Current Day
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(System.currentTimeMillis())
        binding.currentDate.text = currentDate
        updateDateRange(System.currentTimeMillis())
    }

    override fun onStart() {
        super.onStart()
        addWorkoutHistory()
    }

    private fun showDatePickerDialog() {
        val maxDate = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _: DatePicker?, year: Int, month: Int, day: Int ->
                calendar.set(year, month, day)
                updateDateRange(calendar.timeInMillis)
                updateSelectedDate()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

        datePickerDialog.show()
    }

    private fun updateSelectedDate() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val selectedDate = dateFormat.format(calendar.time)
        binding.currentDate.text = selectedDate
    }

    private fun addWorkoutHistory(){
        val workoutHistoryAdapter = WorkoutHistoryAdapter()
        binding.rvWorkoutHistory.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        workoutHistoryViewModel = ViewModelProvider(this).get(WorkoutHistoryViewModel::class.java)

        if (uid != null) {
            workoutHistoryViewModel.getWorkoutHistory(uid).observe(viewLifecycleOwner) { workoutHistories ->
                val filteredList = workoutHistories.filter { it.currentTime in todayStartInMillis..todayEndInMillis }
                workoutHistoryAdapter.addWorkoutHistory(filteredList)
                binding.tvKcal.text = filteredList.sumOf { it.caloriesBurned }.toString()
            }
        }
        binding.rvWorkoutHistory.adapter = workoutHistoryAdapter
    }
    private fun updateDateRange(selectedDateInMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDateInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        todayStartInMillis = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        todayEndInMillis = calendar.timeInMillis
        addWorkoutHistory()
    }

    companion object {
        fun newInstance(): WorkoutHistoryFragment {
            return WorkoutHistoryFragment()
        }
    }
}