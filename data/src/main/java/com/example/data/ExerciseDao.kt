package com.example.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import entity.Exercise

@Dao
interface ExerciseDao {
    @Insert
    fun insert(exercise: Exercise)

    @Query("SELECT * FROM exercise_table")
    fun getAllExercises(): LiveData<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(exercises: List<Exercise>)
}