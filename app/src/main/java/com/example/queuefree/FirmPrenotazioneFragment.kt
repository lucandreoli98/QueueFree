package com.example.queuefree

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_firm_prenotazione.view.*
import kotlinx.android.synthetic.main.prenotazioni_utente.view.*
import java.util.*
import kotlin.collections.ArrayList

class FirmPrenotazioneFragment: Fragment(), DatePickerDialog.OnDateSetListener {

    private var day = 0L
    private var month = 0L
    private var year = 0L
    private var data = ""
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

        this.data = "Clicca per cambiare data\nAttuale: $day/${month+1}/$year"
        //this.data = "Data: $day/$month/$year"
        downloadBooking()
    }

    private fun downloadBooking(){
        val fb = FirebaseDatabaseHelper()
        fb.readFirmFromDB(object: FirebaseDatabaseHelper.DataStatusFirm{
            override fun DataisLoadedFirm(firm: Firm) {
                fb.readBookingFirm(day,month,year,firm,object : FirebaseDatabaseHelper.DataBookingFirm{
                    override fun bookingFirmisLoaded(bookings: ArrayList<Booking>, bookingsHour: ArrayList<Long>, usersID: ArrayList<String>) {
                        vista!!.listFirmPren.visibility = View.VISIBLE
                        vista!!.dataFirmPren.visibility = View.VISIBLE
                        vista!!.dataFirmPren.text = data

                        val adapter =  BookFirmAdapter(context!!, R.layout.list_view_firm_prenotazioni,  bookingsHour, firm)
                        vista!!.listFirmPren.adapter = adapter

                        vista!!.dataFirmPren.setOnClickListener {
                            adapter.clear()
                            vista!!.listFirmPren.visibility = View.INVISIBLE
                            vista!!.dataShowButton.visibility = View.VISIBLE
                            vista!!.dataFirmPren.visibility = View.INVISIBLE
                        }
                        vista!!.listFirmPren.setOnItemClickListener { parent, view, position, l ->
                            if(bookingsHour[position] != 0L){
                                val bundle = Bundle()
                                bundle.putSerializable("firm", firm)
                                bundle.putSerializable("booking", bookings)
                                bundle.putInt("ora", position)
                                bundle.putStringArrayList("usersID", usersID)

                                val intent = Intent(activity, InfoPrenotazioneFirm::class.java)
                                intent.putExtra("parameter", bundle)
                                startActivity(intent)
                            }else
                                Toast.makeText(context!!,"Non ci sono prenotazioni in questa fascia oraria",Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        })
    }
}