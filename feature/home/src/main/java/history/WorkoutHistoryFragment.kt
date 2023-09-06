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
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        //Recycler View for Workout History
        val workoutHistoryAdapter = WorkoutHistoryAdapter()
        binding.rvWorkoutHistory.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

        workoutHistoryViewModel = ViewModelProvider(this).get(WorkoutHistoryViewModel::class.java)
        if (uid != null) {
            workoutHistoryViewModel.getWorkoutHistory(uid).observe(viewLifecycleOwner) { workoutHistories ->
                workoutHistoryAdapter.addWorkoutHistory(workoutHistories)
            }
        }
        binding.rvWorkoutHistory.adapter = workoutHistoryAdapter
    }

    private fun showDatePickerDialog() {
        val maxDate = Calendar.getInstance().apply {
            timeInMillis = calendar.timeInMillis
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _: DatePicker?, year: Int, month: Int, day: Int ->
                calendar.set(year, month, day)
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


    companion object {
        fun newInstance(): WorkoutHistoryFragment {
            return WorkoutHistoryFragment()
        }
    }
}