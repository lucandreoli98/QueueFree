package com.example.queuefree

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PagerAdapter (fm : FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> {
                ShowProfileFragment()
            }
            1 -> {
                PrenotazioniFragment()
            }
            else -> ShowProfileFragment()
        }
    }

    override fun getCount(): Int = 2
}