import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.data.FirebaseQueryLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import entity.KcalByDay
import java.text.SimpleDateFormat
import java.util.Locale

class KcalByDayViewModel : ViewModel() {
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance(
        "https://gofitness-4d8ef-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val kcalByDayRef = database.getReference("KcalByDay")

    fun addKcalData(kcal: Double) {
        requireNotNull(userId) { "User not authenticated" }
        val currentTimeMillis = System.currentTimeMillis()

        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        val currentDate = dateFormat.format(currentTimeMillis)

        val kcalData = KcalByDay(kcal, currentTimeMillis)

        val kcalDataRef = userId.let { kcalByDayRef.child(it).child("kcalData") }
        val dateRef = kcalDataRef.child(currentDate)
        val timestampStr = currentTimeMillis.toString()

        dateRef.child(timestampStr).setValue(kcalData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Success Firebase Insertion", "Success Insert Kcal Data")
                } else {
                    Log.d("Check Firebase", "Error writing kcal data")
                }
            }
    }

    fun getKcalByDayForDate(userId: String, date: String): LiveData<List<KcalByDay>> {
        val kcalDataRef = userId.let { kcalByDayRef.child(it).child("kcalData").child(date) }

        val liveData = FirebaseQueryLiveData(kcalDataRef.orderByChild("timestamp"))

        return liveData.map { dataSnapshot ->
            val kcalByDayList = mutableListOf<KcalByDay>()
            for (kcalSnapshot in dataSnapshot.children) {
                val kcalData = kcalSnapshot.getValue(KcalByDay::class.java)
                kcalData?.let {
                    kcalByDayList.add(it)
                }
            }
            kcalByDayList
        }
    }
}