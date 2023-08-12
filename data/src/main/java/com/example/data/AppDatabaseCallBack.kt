package com.example.data

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import entity.Exercise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        val exerciseDao = AppDatabase.getInstance(context).exerciseDao()

        CoroutineScope(Dispatchers.IO).launch {
            val defaultExercises = listOf(
                Exercise(name = "Running", duration = 30, caloriesBurned = 30.0, definition = "Running is ...", goodFor = "...", rep = 0),
                Exercise(name = "Cycling", duration = 45, caloriesBurned = 30.0, definition = "Cycling is ...", goodFor = "...", rep = 0)
            )

            exerciseDao.insertAll(defaultExercises)
        }
    }
}
