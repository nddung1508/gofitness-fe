import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.data.FirebaseQueryLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import entity.Step
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StepViewModel : ViewModel() {
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance(
        "https://gofitness-4d8ef-default-rtdb.asia-southeast1.firebasedatabase.app/"
    )
    private val stepRef = database.getReference("steps")

    fun addStep(amount: Int) {
        userId?.let {
            val currentTimestamp = System.currentTimeMillis()
            val currentDateString = getCurrentDateFormat(currentTimestamp)
            val stepsForDateRef = stepRef.child(it).child("stepsData").child(currentDateString)
            val query = stepsForDateRef.orderByChild("timestamp")

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val stepEntries = mutableListOf<Pair<String, Step>>()
                    for (stepSnapshot in snapshot.children) {
                        val key = stepSnapshot.key
                        val stepData = stepSnapshot.getValue(Step::class.java)
                        if (key != null && stepData != null) {
                            stepEntries.add(Pair(key, stepData))
                        }
                    }
                    stepEntries.sortByDescending { it.second.timestamp }
                    val entriesToKeep = stepEntries.take(10)
                    stepsForDateRef.removeValue()
                    for ((key, step) in entriesToKeep) {
                        stepsForDateRef.child(key).setValue(step)
                    }
                    val stepEntryKey = stepsForDateRef.push().key
                    val step = Step(amount, currentTimestamp)
                    stepEntryKey?.let { key ->
                        stepsForDateRef.child(key).setValue(step)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    fun getStepsForDate(userId: String, date: String): LiveData<List<Step>> {
        val stepsForDateRef = stepRef.child(userId).child("stepsData").child(date)

        val liveData = FirebaseQueryLiveData(stepsForDateRef.orderByChild("timestamp"))

        return liveData.map { dataSnapshot ->
            val stepList = mutableListOf<Step>()
            for (stepSnapshot in dataSnapshot.children) {
                val stepData = stepSnapshot.getValue(Step::class.java)
                stepData?.let {
                    stepList.add(it)
                }
            }
            stepList
        }
    }

    private fun createQuery(databaseReference: DatabaseReference?): DatabaseReference {
        return databaseReference ?: stepRef
    }

    private fun getCurrentDateFormat(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}