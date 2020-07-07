package com.example.queuefree

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.DatePicker

import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_letsbook.view.*
import java.util.*
import kotlin.collections.ArrayList

class LetsbookFragment: Fragment(), DatePickerDialog.OnDateSetListener {

    private var day = 0L
    private var month = 0L
    private var year = 0L
    private var v:View? = null
    private val fb = FirebaseDatabaseHelper()

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

                val partArray : ArrayList<String> = ArrayList()
                for(i in 1..firm.maxPartecipants)
                    partArray.add(i.toString())
                val b = ArrayAdapter(context!!,android.R.layout.simple_spinner_item,partArray)
                b.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.npeople.adapter = b

                val durataArray : ArrayList<String> = ArrayList()
                for(i in 1..firm.maxTurn)
                    durataArray.add(i.toString())
                val c = ArrayAdapter(context!!,android.R.layout.simple_spinner_item,durataArray)
                c.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.durataH.adapter = c

                view.book.setOnClickListener{
                    val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
                    if(day == 0L || month == 0L || year == 0L){
                        view.select_data.text = resources.getString(R.string.dataEmpty)
                        view.select_data.requestFocus()
                    }else {
                        view.startHour.visibility=View.VISIBLE

                        val hoursArray : ArrayList<String> = ArrayList()
                        hoursArray.add(completeTimeStamp(firm.startHour,firm.startMinute))
                        for(i in firm.startHour+1 until firm.endHour)
                            hoursArray.add(completeTimeStamp(i,firm.startMinute))
                        val a = SpinnerAdapter(context!!,hoursArray, null)
                        a.setDropDownViewResource(R.layout.spinner_item)
                        view.startHour.adapter = a

                        /*fb.readDailyBooking(day,month,year,firm, object: FirebaseDatabaseHelper.DataStatusBooking{
                            override fun BookingisLoaded(bookings: ArrayList<Long>) {

                            }
                        })
                        for(i in view.startHour.selectedItemPosition+1..view.durataH.selectedItemPosition+view.startHour.selectedItemPosition+2){
                            val r = Booking(day,month,year,i.toLong(),(view.npeople.selectedItemPosition+1).toLong()) //enri gay
                            FirebaseDatabase.getInstance().getReference("/bookings/${firm.id}/$id-$i").setValue(r)
                        }*/
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