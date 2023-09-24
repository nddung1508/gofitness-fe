import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.data.FirebaseQueryLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import entity.WorkoutHistory

class WorkoutHistoryViewModel : ViewModel() {
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance(
            "https://gofitness-4d8ef-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val workoutHistoryDatabaseRef = database.getReference("workoutHistory")

    fun getWorkoutHistory(userId: String): LiveData<List<WorkoutHistory>> {
        requireNotNull(userId) { "User not authenticated" }
        val query = workoutHistoryDatabaseRef.child(userId)
        val liveData = FirebaseQueryLiveData(query)

        return liveData.map { dataSnapshot ->
            val workoutHistories = mutableListOf<WorkoutHistory>()
            for (workoutSnapshot in dataSnapshot.children) {
                val workoutHistory = workoutSnapshot.getValue(WorkoutHistory::class.java)
                workoutHistory?.let {
                    workoutHistories.add(it)
                }
            }
            workoutHistories
        }
    }

    fun addWorkoutHistory(
        name: String,
        image: List<Byte>,
        duration: Long,
        amountOfExercise: Int,
        caloriesBurned: Int,
        currentTime : Long
    ) {
        requireNotNull(userId) { "User not authenticated" }
        val byteArray = image.toByteArray()
        val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)
        val workoutHistory = WorkoutHistory(
            name = name,
            image = base64Image,
            duration = duration,
            amountOfExercise = amountOfExercise,
            caloriesBurned = caloriesBurned,
            currentTime = currentTime
        )
        workoutHistoryDatabaseRef.child(userId).push().setValue(workoutHistory)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Success Firebase Insertion", "Success Insert Into Realtime Database")
                } else {
                    Log.d("Check Firebase", "Error writing workout history")
                }
            }
    }
}