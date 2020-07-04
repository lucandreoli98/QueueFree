package com.example.queuefree

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FirmProfileFragment: Fragment() {



    private val fb : FirebaseDatabaseHelper = FirebaseDatabaseHelper()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_firm_profile, container, false)

        /*fb!!.readFirmsandtakeAdress(object : FirebaseDatabaseHelper.DataStatus{

        })*/


        return view
    }
}