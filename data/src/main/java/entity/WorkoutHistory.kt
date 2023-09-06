package entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@Entity
@IgnoreExtraProperties
data class WorkoutHistory(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val name : String =  "",
    val image : String =  "",
    val duration: Long = 0,
    val amountOfExercise : Int = 0,
    val caloriesBurned : Int = 0,
    val currentTime : Long = 0
    )