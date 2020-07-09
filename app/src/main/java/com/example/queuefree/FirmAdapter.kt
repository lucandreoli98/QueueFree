package com.example.queuefree

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FirmAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> {
                FirmProfileFragment()
            }
            1 -> {
                PrenotazioniFragment()
            }
            else -> FirmProfileFragment()
        }
    }

    override fun getCount(): Int = 2
}