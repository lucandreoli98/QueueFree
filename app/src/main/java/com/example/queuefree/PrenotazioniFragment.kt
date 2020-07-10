package com.example.queuefree

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.prenotazioni_utente.view.*

class PrenotazioniFragment: Fragment() {
    val database=FirebaseDatabaseHelper()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.prenotazioni_utente, container, false)
        val resources=R.layout.list_view_prenotazioni
        database.readBookingUser(object: FirebaseDatabaseHelper.DataBookingUser{
            override fun BookingUserisLoaded(bookings:  ArrayList<BookingUser>) {
                for (Bu in bookings) {
                    val adapter = context?.let { BookAdapter(it, R.layout.list_view_prenotazioni, Bu.bookings, Bu.firm)
                    }
                    view.itemslistView.setAdapter(adapter)
                    // view.prenotazioni_mie.setText(database.bookings.get(0).nPartecipanti.toString())
                }
            }


        })






        return view
    }
}