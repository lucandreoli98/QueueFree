package com.example.queuefree

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.Intent
import android.util.Log
import android.widget.Toast
import android.widget.ArrayAdapter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_letsbook.*
import kotlinx.android.synthetic.main.fragment_letsbook.view.*
import kotlinx.android.synthetic.main.fragment_register_firm.view.*

class LetsbookFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_letsbook, container, false)

        FirebaseDatabaseHelper().readFirmsfromEmail(arguments!!.getString("email",""), object : FirebaseDatabaseHelper.DataStatusFirm {
            override fun DataisLoadedFirm(firm: Firm){
                view.firmName.text=firm.nomeazienza
                if(firm.startHour<10){
                    if(firm.startMinute<10){
                        view.startHou.text = " 0${firm.startHour}:0${firm.startMinute}"
                    } else {
                        view.startHou.text = " 0${firm.startHour}:${firm.startMinute}"
                    }
                } else if(firm.startMinute<10){
                    view.startHou.text = " ${firm.startHour}:0${firm.startMinute}"
                } else {
                    view.startHou.text = " ${firm.startHour}:${firm.startMinute}"
                }
                if(firm.endHour<10){
                    if(firm.endMinute<10){
                        view.endHou.text = " 0${firm.endHour}:0${firm.endMinute}"
                    } else {
                        view.endHou.text = " 0${firm.endHour}:${firm.endMinute}"
                    }
                } else if(firm.endMinute<10){
                    view.endHou.text = " ${firm.endHour}:0${firm.endMinute}"
                } else {
                    view.endHou.text = " ${firm.endHour}:${firm.endMinute}"
                }

                val hoursArray : ArrayList<String> = ArrayList()
                hoursArray.add(firm.startHour.toString())
                for(i in firm.startHour+1 until firm.endHour){
                    hoursArray.add(i.toString())
                }
                val a = ArrayAdapter(context!!,android.R.layout.simple_spinner_item,hoursArray)
                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                view.startHour.adapter=a




            }
        })

        return view
    }
}