package com.example.queuefree

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_firm_prenotazione.view.*
import kotlinx.android.synthetic.main.prenotazioni_utente.view.*
import java.util.*
import kotlin.collections.ArrayList

class FirmPrenotazioneFragment: Fragment(), DatePickerDialog.OnDateSetListener {

    private var day = 0L
    private var month = 0L
    private var year = 0L
    private var vista: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vista = inflater.inflate(R.layout.fragment_firm_prenotazione, container, false)

        vista!!.dataShowButton.setOnClickListener {
            showDatePickerDialog() // apre il pannello del calendario sulla data di oggi
        }

        return vista
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

        vista!!.dataShowButton.visibility = View.INVISIBLE

        downloadBooking()
    }

    private fun downloadBooking(){
        val fb = FirebaseDatabaseHelper()
        Log.d("Data: ", day.toString()+month.toString()+year.toString())
        fb.readFirmFromDB(object: FirebaseDatabaseHelper.DataStatusFirm{
            override fun DataisLoadedFirm(firm: Firm) {
                fb.readBookingFirm(day,month,year,firm,object : FirebaseDatabaseHelper.DataBookingFirm{
                    override fun bookingFirmisLoaded(bookings: ArrayList<Booking>, bookingsHour: ArrayList<Long>) {
                        vista!!.listFirmPren.visibility = View.VISIBLE

                        val adapter =  BookFirmAdapter(context!!, R.layout.list_view_firm_prenotazioni,  bookingsHour, firm)
                        vista!!.listFirmPren.adapter = adapter

                        vista!!.listFirmPren.setOnItemClickListener { parent, view, position, l ->


                            /*val bundle = Bundle()
                            bundle.putSerializable("firm", totalfirmcompact[position])
                            bundle.putSerializable("book", parent!!.getItemAtPosition(position) as Booking)
                            bundle.putLong("durata", durate[position])*/

                        }
                    }
                })
            }
        })
    }
}