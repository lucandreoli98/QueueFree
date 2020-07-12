package com.example.queuefree

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_firm_prenotazione.view.*
import kotlinx.android.synthetic.main.fragment_show_profile.*
import java.util.*

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
    }
}