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

                            for (bu in bookingUser)
                                for(b in bu.bookings){
                                    totalbu.add(b)
                                    totalFirm.add(bu.firm)
                                }

                            var startd = 0L
                            var startm = 0L
                            var starty = 0L
                            var count = 0L
                            var difila = 0L
                            var startfirm = ""
                            var totalbucompact = ArrayList<Booking>()
                            var totalfirmcompact = ArrayList<Firm>()
                            var durate = ArrayList<Long>()

                            for(i in 0 until totalbu.size){
                                if(totalbu[i].dd != startd || totalbu[i].mm != startm || totalbu[i].yy != starty || startfirm != totalFirm[i].nomeazienza || totalbu[i].nOre != (count+1)){
                                    startd = totalbu[i].dd
                                    startm = totalbu[i].mm
                                    starty = totalbu[i].yy
                                    totalbucompact.add(totalbu[i])
                                    totalfirmcompact.add(totalFirm[i])
                                    durate.add(difila)
                                    difila=0
                                    Log.d("BOOK:", "${count+1} ${totalbu[i].nOre}")
                                    Log.d("BOOK:", "${totalFirm[i].nomeazienza} ${totalbu[i].dd} ${totalbu[i].mm} ${totalbu[i].yy} ${totalbu[i].nPartecipanti} ${totalbu[i].nOre+totalFirm[i].startHour}-${totalbu[i].nOre+totalFirm[i].startHour+durate[i]}")
                                }else{
                                    difila++
                                    Log.d("BOOK:", difila.toString())

                                }
                                count = totalbu[i].nOre

                            }
                            val adapter =  BookAdapter(context!!, R.layout.list_view_prenotazioni, totalbu, totalFirm, durate)

                            view.itemslistView.adapter = adapter
                        }
                    }
                })
            }
        })

        return view
    }
}