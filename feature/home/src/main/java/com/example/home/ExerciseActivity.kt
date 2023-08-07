package com.example.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.home.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity(){
    private lateinit var binding : ActivityExerciseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addFragment(ExerciseTypeFragment())
    }

    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.inTransaction {
            replace(R.id.exercise_fragment_container, fragment)
        }
    }

    fun addFragment(fragment: Fragment) {
        supportFragmentManager.inTransaction {
            add(R.id.exercise_fragment_container, fragment)
            addToBackStack(null)
        }
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val ft = beginTransaction()
        ft.func()
        ft.commit()
    }
}