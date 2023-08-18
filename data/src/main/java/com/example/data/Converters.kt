package com.example.data

import androidx.room.TypeConverter
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import entity.Exercise

class Converters {
    @TypeConverter
    fun fromExerciseList(exerciseList: List<Exercise>): String {
        val gson = Gson()
        return gson.toJson(exerciseList)
    }

    @TypeConverter
    fun toExerciseList(exerciseListString: String): List<Exercise> {
        val gson = Gson()
        val type = object : TypeToken<List<Exercise>>() {}.type
        return gson.fromJson(exerciseListString, type)
    }
}