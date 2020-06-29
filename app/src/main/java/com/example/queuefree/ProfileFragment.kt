package com.example.queuefree

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var mViewPager : ViewPager
    private lateinit var mPagerAdapter : PagerAdapter
    private lateinit var profileButton : ImageButton
    private lateinit var prenotButton : ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        mViewPager = view.findViewById(R.id.viewPagerID)
        profileButton = view.findViewById<ImageButton>(R.id.profileBtn)
        prenotButton = view.findViewById<ImageButton>(R.id.prenotBtn)

        mPagerAdapter = PagerAdapter(fragmentManager!!)
        mViewPager.adapter = mPagerAdapter
        mViewPager.offscreenPageLimit = 2

        // default tab
        mViewPager.currentItem = 0
        profileButton.setImageResource(R.drawable.ic_person_red)

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                changingTabs(position)
            }

        })

        return view
    }

    private fun changingTabs(position: Int){
        if(position == 0){
            profileButton.setImageResource(R.drawable.ic_person_red)
            prenotButton.setImageResource(R.drawable.ic_event_black)
        }
        else if(position == 1){
            profileButton.setImageResource(R.drawable.ic_profile)
            prenotButton.setImageResource(R.drawable.ic_event_red)
        }
    }
}