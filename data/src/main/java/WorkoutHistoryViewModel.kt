import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.data.FirebaseQueryLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import entity.KcalByDay
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


    fun addKcalData(userId: String, kcal: Double, timestamp: Long) {
        val kcalData = KcalByDay(kcal, timestamp)
        val kcalDataRef = workoutHistoryDatabaseRef.child(userId).child("kcalData")
        val timestampStr = timestamp.toString() // Convert timestamp to String for use as a key
        kcalDataRef.child(timestampStr).setValue(kcalData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Success Firebase Insertion", "Success Insert Kcal Data")
                } else {
                    Log.d("Check Firebase", "Error writing kcal data")
                }
            }
    }

    fun getTotalKcalForDate(userId: String, dateMillis: Long): LiveData<Double> {
        val dateString = dateMillis.toString()
        val kcalDataRef = workoutHistoryDatabaseRef.child(userId).child("kcalData")

        val liveData = FirebaseQueryLiveData(kcalDataRef.orderByChild("timestamp").equalTo(dateString))

        return liveData.map { dataSnapshot ->
            var totalKcal = 0.0
            for (kcalSnapshot in dataSnapshot.children) {
                val kcalData = kcalSnapshot.getValue(KcalByDay::class.java)
                kcalData?.let {
                    totalKcal += it.kcal
                }
            }
            totalKcal
        }
    }
}