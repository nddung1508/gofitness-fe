import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.data.FirebaseQueryLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import entity.PersonalInformation

class PersonalInformationViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance("https://gofitness-4d8ef-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun addPersonalInformation(height: Int, weight: Int, goal: String) {
        requireNotNull(userId) { "User not authenticated" }
        val personalInfo = PersonalInformation(height, weight, goal)
        val personalInfoRef = userId.let { database.getReference("PersonalInformation").child(it) }
        personalInfoRef.setValue(personalInfo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Success Firebase Insertion", "Success Insert Personal Information")
                } else {
                    Log.d("Check Firebase", "Error writing personal information")
                }
            }
    }

    fun getPersonalInformation(): LiveData<PersonalInformation?> {
        requireNotNull(userId) { "User not authenticated" }
        val personalInfoRef = userId.let { database.getReference("PersonalInformation").child(it) }
        val liveData = FirebaseQueryLiveData(personalInfoRef)
        return liveData.map { dataSnapshot ->
            dataSnapshot.getValue(PersonalInformation::class.java)
        }
    }
}