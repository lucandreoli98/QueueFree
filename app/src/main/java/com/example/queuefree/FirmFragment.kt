package com.example.queuefree

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_firm_profile.view.*
import kotlin.math.sin

class FirmFragment : Fragment() {

    private var v: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_firm_profile, container, false)

        v!!.viewFirmPagerID.adapter = FirmAdapter(fragmentManager!!)
        v!!.viewFirmPagerID.offscreenPageLimit = 2

        // default tab
        v!!.viewFirmPagerID.currentItem = 0
        v!!.profileFirmBtn.setImageResource(R.drawable.ic_person_red)

        v!!.viewFirmPagerID.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                changingTabs(position)
            }

        })

        return v
    }

    private fun changingTabs(position: Int){
        if(position == 0){
            v!!.profileFirmBtn.setImageResource(R.drawable.ic_person_red)
            v!!.prenotFirmBtn.setImageResource(R.drawable.ic_event_black)
        }
        else if(position == 1){
            v!!.profileFirmBtn.setImageResource(R.drawable.ic_profile)
            v!!.prenotFirmBtn.setImageResource(R.drawable.ic_event_red)
        }
    }
}