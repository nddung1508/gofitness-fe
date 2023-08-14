package entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_table")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val duration: Int,
    val caloriesBurned: Double,
    val definition : String,
    val goodFor : String,
    val rep: Int = 0,
    val image: ByteArray?
)