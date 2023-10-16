import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.data.FirebaseQueryLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import entity.Running
import java.text.SimpleDateFormat
import java.util.Locale

class RunningViewModel : ViewModel() {
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance("https://your-firebase-url.firebaseio.com/")
    private val runningRef = database.getReference("Running")

    fun addRunningData(kcal: Int, duration: Int, distance: Int) {
        requireNotNull(userId) { "User not authenticated" }
        val currentTimeMillis = System.currentTimeMillis()

        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        val currentDate = dateFormat.format(currentTimeMillis)

        val runningData = Running(kcal, duration, distance, currentTimeMillis)

        val runningDataRef = userId.let { runningRef.child(it).child("runningData") }
        val dateRef = runningDataRef.child(currentDate)
        val timestampStr = currentTimeMillis.toString()

        dateRef.child(timestampStr).setValue(runningData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Success Firebase Insertion", "Success Insert Running Data")
                } else {
                    Log.d("Check Firebase", "Error writing running data")
                }
            }
    }

    fun getRunningDataForDate(userId: String, date: String): LiveData<List<Running>> {
        val runningDataRef = userId.let { runningRef.child(it).child("runningData").child(date) }

        val liveData = FirebaseQueryLiveData(runningDataRef.orderByChild("dateInMillis"))

        return liveData.map { dataSnapshot ->
            val runningList = mutableListOf<Running>()
            for (runningSnapshot in dataSnapshot.children) {
                val runningData = runningSnapshot.getValue(Running::class.java)
                runningData?.let {
                    runningList.add(it)
                }
            }
            runningList
        }
    }
}