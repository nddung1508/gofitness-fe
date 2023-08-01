package com.example.gofitness.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class NavBarAdapter(fragmentManager: Fragment) :
    FragmentStateAdapter(fragmentManager) {
    private val fragments: ArrayList<Fragment> = arrayListOf()

    fun addFragments(fragment: Fragment) {
        fragments.add(fragment)
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}