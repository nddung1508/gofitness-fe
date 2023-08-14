package com.example.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import entity.Workout

@Dao
interface WorkoutDao {
    @Insert
    fun insertWorkout(workout: Workout): Long

    @Query("SELECT * FROM workouts")
    fun getAllWorkouts(): LiveData<List<Workout>>
}