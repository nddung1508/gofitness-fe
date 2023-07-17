package com.example.gofitness.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.gofitness.R
import com.example.gofitness.databinding.ActivityMainBinding
import com.example.gofitness.ui.splash.SplashFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        navigateToFragment(SplashFragment())
    }

    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.inTransaction {
            replace(R.id.main_fragment_container, fragment)
        }
    }

    fun addFragment(fragment: Fragment) {
        supportFragmentManager.inTransaction {
            add(R.id.main_fragment_container, fragment)
            addToBackStack(null)
        }
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val ft = beginTransaction()
        ft.func()
        ft.commit()
    }
}