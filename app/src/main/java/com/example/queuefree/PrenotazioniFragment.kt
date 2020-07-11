package com.example.queuefree

import android.os.Bundle
import android.util.Log
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

        database.readAllFirmFromDB(object : FirebaseDatabaseHelper.DataStatusHashFirm{
            override fun dataisLoadedFirm(firms: HashMap<String, Firm>) {

                database.readBookingUser(firms, object: FirebaseDatabaseHelper.DataBookingUser{
                    override fun BookingUserisLoaded(bookingUser:  ArrayList<BookingUser>) {
                        //Log.d("DATI ARRIVATI", bookingUser[0].firm.nomeazienza)
                        for (Bu in bookingUser) {
                            val adapter = context?.let {
                                BookAdapter(it, R.layout.list_view_prenotazioni, Bu.bookings, Bu.firm)
                            }
                            view.itemslistView.adapter = adapter
                        }
                    }
                })
            }
        })

        return view
    }
    }