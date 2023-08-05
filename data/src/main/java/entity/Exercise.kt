package entity

import androidx.room.PrimaryKey

data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val duration: Int,
    val caloriesBurned: Double,
    val detailDescription : String
)