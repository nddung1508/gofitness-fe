package entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WorkoutHistory(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val name : String,
    val image : String,
    val duration: Long,
    val amountOfExercise : Int,
    val caloriesBurned : Int,
    val currentTime : Long
    )