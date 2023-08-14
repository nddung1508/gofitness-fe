package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import entity.Exercise
import entity.Workout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class AppDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        val pushUp = R.drawable.push_up
        val pullUp = R.drawable.pull_up

        val workoutDao = AppDatabase.getInstance(context).workoutDao()

        CoroutineScope(Dispatchers.IO).launch {
            val armExercise = listOf(
                Exercise(name = "Push Up", duration = 30, caloriesBurned = 30.0, definition = "Push up is ...", goodFor = "...", rep = 0, image = ConvertResourceToByteArray(pushUp)),
                Exercise(name = "Pull Up", duration = 45, caloriesBurned = 30.0, definition = "Pull up is ...", goodFor = "...", rep = 0, image = ConvertResourceToByteArray(pullUp))
            )

            val armWorkout = Workout(name = "Arm Workout", exercises = armExercise)

            workoutDao.insertWorkout(armWorkout)
        }
    }
    private fun ConvertResourceToByteArray(imageResource: Int): ByteArray {
        val imageBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, imageResource)
        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
