package com.example.queuefree

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.Intent
import android.util.Log
import android.widget.Toast
import android.widget.ArrayAdapter
import android.widget.DatePicker

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_letsbook.*
import kotlinx.android.synthetic.main.fragment_letsbook.view.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register_firm.view.*
import java.util.*
import kotlin.collections.ArrayList

class LetsbookFragment: Fragment(), DatePickerDialog.OnDateSetListener {

    private var day = 0L
    private var month = 0L
    private var year = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_letsbook, container, false)

        FirebaseDatabaseHelper().readFirmsfromEmail(arguments!!.getString("email",""), object : FirebaseDatabaseHelper.DataStatusFirm {
            override fun DataisLoadedFirm(firm: Firm){
                view.firmName.text=firm.nomeazienza

                view.select_data.setOnClickListener {
                    showDatePickerDialog() // apre il pannello del calendario sulla data di oggi
                }

                view.startHou.text = completeTimeStamp(firm.startHour,firm.startMinute)
                view.endHou.text = completeTimeStamp(firm.endHour,firm.endMinute)

                val hoursArray : ArrayList<String> = ArrayList()
                hoursArray.add(completeTimeStamp(firm.startHour,firm.startMinute))
                for(i in firm.startHour+1 until firm.endHour){
                    hoursArray.add(completeTimeStamp(i,firm.startMinute))
                }
                val a = ArrayAdapter(context!!,android.R.layout.simple_spinner_item,hoursArray)
                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                view.startHour.adapter=a




            }
        })

        return view
    }

    fun completeTimeStamp(hour :Long,minute: Long):String{
        return if(hour<10){
            if(minute<10){
                "0${hour}:0${minute}"
            } else {
                "0${hour}:${minute}"
            }
        } else if(minute<10){
            "${hour}:0${minute}"
        } else {
            "${hour}:${minute}"
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(activity!!, this,
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )

        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        this.day = dayOfMonth.toLong()
        this.month = (month + 1).toLong()
        this.year = year.toLong()
        val date = dayOfMonth.toString() + " / " + (month + 1) + " / " + year
        register_show_calendar.text = date
    }
}