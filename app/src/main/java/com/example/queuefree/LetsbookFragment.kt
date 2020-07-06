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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
    private var v:View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_letsbook, container, false)
        v=view

        FirebaseDatabaseHelper().readFirmsfromEmail(arguments!!.getString("email",""), object : FirebaseDatabaseHelper.DataStatusFirm {
            override fun DataisLoadedFirm(firm: Firm){
                view.firmName.text=firm.nomeazienza

                view.startHou.text = completeTimeStamp(firm.startHour,firm.startMinute)
                view.endHou.text = completeTimeStamp(firm.endHour,firm.endMinute)

                view.select_data.setOnClickListener {
                    showDatePickerDialog() // apre il pannello del calendario sulla data di oggi
                }


                val hoursArray : ArrayList<String> = ArrayList()
                hoursArray.add(completeTimeStamp(firm.startHour,firm.startMinute))
                for(i in firm.startHour+1 until firm.endHour)
                    hoursArray.add(completeTimeStamp(i,firm.startMinute))
                val a = ArrayAdapter(context!!,android.R.layout.simple_spinner_item,hoursArray)
                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.startHour.adapter = a

                val partArray : ArrayList<String> = ArrayList()
                for(i in 1..firm.maxPartecipants)
                    partArray.add(i.toString())
                val b = ArrayAdapter(context!!,android.R.layout.simple_spinner_item,partArray)
                b.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.npeople.adapter = b

                val durataArray : ArrayList<String> = ArrayList()
                for(i in 1..firm.maxTurn)
                    partArray.add(i.toString())
                val c = ArrayAdapter(context!!,android.R.layout.simple_spinner_item,durataArray)
                c.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.npeople.adapter = c

                view.book.setOnClickListener{
                    val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
                    if(day == 0L || month == 0L || year == 0L){
                        view.select_data.text = resources.getString(R.string.dataEmpty)
                        view.select_data.requestFocus()
                    }else {
                        val r = Booking(id,day,month,year)
                        //TODO: aggiungere n prenotazioni tante quante le ore di durata(ciclo for) e il numero di partecipanti
                        FirebaseDatabase.getInstance().getReference("/bookings/${firm.email}/$id").setValue(r)
                    }
                }
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
        v!!.select_data.text = date
    }
}