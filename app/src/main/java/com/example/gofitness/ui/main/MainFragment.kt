package com.example.gofitness.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.gofitness.R
import com.example.gofitness.databinding.FragmentMainBinding
import com.example.gofitness.ui.MainActivity
import com.example.gofitness.ui.splash.SplashFragment
import com.example.home.HomeFragment
import com.example.profile.ProfileFragment
import com.example.profile.ProfileNavigator
import com.example.statistics.StatisticFragment
import history.WorkoutHistoryFragment

class MainFragment : Fragment(), ProfileNavigator {
    private lateinit var binding : FragmentMainBinding
    private val workoutHistoryFragment = WorkoutHistoryFragment.newInstance()
    private val homeFragment = HomeFragment.newInstance()
    private val profileFragment = ProfileFragment.newInstance()
    private val statisticFragment = StatisticFragment.newInstance()
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
        pagerAdapter.addFragments(statisticFragment)
        pagerAdapter.addFragments(workoutHistoryFragment)
        profileFragment.profileNavigator = this
        pagerAdapter.addFragments(profileFragment)
        viewPager.adapter = pagerAdapter
    }
    private fun initBottomMenu() {
        binding.mainBottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> viewPager.currentItem = 0
                R.id.nav_statistic -> viewPager.currentItem = 1
                R.id.nav_workout_history -> viewPager.currentItem = 2
                R.id.nav_profile -> viewPager.currentItem = 3
            }
            return@setOnItemSelectedListener true
        }
    }
    override fun navigateScreen() {
        val splashFragment = SplashFragment()
        (requireActivity() as MainActivity).navigateToFragment(splashFragment)
    }
    companion object{
        const val LOG_OUT = "MAIN_TO_LOGIN"
    }
}