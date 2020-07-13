package com.example.queuefree

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class InfoTicket: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.info_prenotazione, container, false)

        val firm: Firm = arguments!!.getSerializable("firm") as Firm
        val booking: Booking = arguments!!.getSerializable("book") as Booking
        val durata: Long = arguments!!.getLong("durata")



        return view
    }
}