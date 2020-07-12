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

        database.readAllFirmFromDB(object : FirebaseDatabaseHelper.DataStatusHashFirm{
            override fun dataisLoadedFirm(firms: HashMap<String, Firm>) {

                database.readBookingUser(firms, object: FirebaseDatabaseHelper.DataBookingUser{
                    override fun BookingUserisLoaded(bookingUser:  ArrayList<BookingUser>) {
                        if(context != null){
                            val totalbu = ArrayList<Booking>()
                            val totalFirm = ArrayList<Firm>()

                            for (bu in bookingUser) {
                                Log.e("AAAAAAAAAAAAAAAAAAAAAAAAAAa",bu.firm.nomeazienza + " " + bu.bookings.size + bu.bookings[0].dd.toString())

                                for(b in bu.bookings){
                                    Log.e("AAAAAAAAAAAAAAAAAAAAAAAAAAAAa","AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAZ")
                                    totalbu.add(b)
                                    totalFirm.add(bu.firm)
                                }
                            }
                            val adapter =  BookAdapter(context!!, R.layout.list_view_prenotazioni, totalbu, totalFirm)

                            view.itemslistView.adapter = adapter
                        }
                    }
                })
            }
        })

        return view
    }
}