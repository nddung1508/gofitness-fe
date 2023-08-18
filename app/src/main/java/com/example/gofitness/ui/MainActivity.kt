package com.example.gofitness.ui

import android.database.CursorWindow
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.gofitness.R
import com.example.gofitness.databinding.ActivityMainBinding
import com.example.gofitness.ui.splash.SplashFragment
import java.lang.reflect.Field


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.isAccessible = true
            field.set(null, 100 * 1024 * 1024) //the 100MB is the new size
        } catch (e: Exception) {
            e.printStackTrace()
        }

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