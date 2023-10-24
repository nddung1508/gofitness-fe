import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.data.FirebaseQueryLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import entity.Running

class RunningViewModel : ViewModel() {
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance(
        "https://gofitness-4d8ef-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val runningDatabaseRef = database.getReference("running")

    fun getRunningHistory(userId: String): LiveData<List<Running>> {
        requireNotNull(userId) { "User not authenticated" }
        val query = runningDatabaseRef.child(userId)
        val liveData = FirebaseQueryLiveData(query)

        return liveData.map { dataSnapshot ->
            val runningHistory = mutableListOf<Running>()
            for (runningSnapshot in dataSnapshot.children) {
                val running = runningSnapshot.getValue(Running::class.java)
                running?.let {
                    runningHistory.add(it)
                }
            }
            runningHistory
        }
    }

    fun addRunningHistory(
        kcal: Double,
        duration: Int,
        distance: Double,
        dateInMillis: Long,
        polylines: List<String>
    ) {
        requireNotNull(userId) { "User not authenticated" }
        val running = Running(
            kcal = kcal,
            duration = duration,
            distance = distance,
            dateInMillis = dateInMillis,
            polylines = polylines
        )
        runningDatabaseRef.child(userId).push().setValue(running)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Success Firebase Insertion", "Success Insert Into Realtime Database")
                } else {
                    Log.d("Check Firebase", "Error writing running history")
                }
            }
    }
}