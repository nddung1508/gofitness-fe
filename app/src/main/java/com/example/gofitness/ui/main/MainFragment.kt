package com.example.gofitness.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.gofitness.R
import com.example.gofitness.databinding.FragmentMainBinding
import com.example.home.HomeFragment

class MainFragment : Fragment(){
    private lateinit var binding : FragmentMainBinding
    private val homeFragment = HomeFragment.newInstance()
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: NavBarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewFragment()
        initBottomMenu()
    }

    private fun initViewFragment() {
        viewPager = binding.mainFragmentContainer
        viewPager.offscreenPageLimit = 4
        viewPager.isUserInputEnabled = false
        pagerAdapter = NavBarAdapter(this)
        pagerAdapter.addFragments(homeFragment)

        viewPager.adapter = pagerAdapter
    }
    private fun initBottomMenu() {
        binding.mainBottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> viewPager.currentItem = 0
            }
            return@setOnItemSelectedListener true
        }
    }

    companion object{
        const val LOG_OUT = "MAIN_TO_LOGIN"
    }
}