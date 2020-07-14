package com.example.queuefree

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.updateLayoutParams
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
                            totalbu.clear()
                            val totalFirm = ArrayList<Firm>()
                            totalFirm.clear()

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
                            var startParte = 0L
                            var startfirm = ""
                            val totalbucompact = ArrayList<Booking>()
                            totalbucompact.clear()
                            val totalfirmcompact = ArrayList<Firm>()
                            totalfirmcompact.clear()
                            val durate = ArrayList<Long>()

                            for(i in 0 until totalbu.size){
                                if(totalbu[i].dd != startd || totalbu[i].mm != startm || totalbu[i].yy != starty || totalbu[i].nPartecipanti != startParte || startfirm != totalFirm[i].nomeazienza || totalbu[i].nOre != (count+1)){
                                    startd = totalbu[i].dd
                                    startm = totalbu[i].mm
                                    starty = totalbu[i].yy
                                    startParte = totalbu[i].nPartecipanti
                                    startfirm =  totalFirm[i].nomeazienza
                                    totalbucompact.add(totalbu[i])
                                    totalfirmcompact.add(totalFirm[i])
                                    if(difila!=0L)
                                        durate.add(difila)

                                    difila=1
                                }else
                                    difila++

                                count = totalbu[i].nOre
                            }
                            durate.add(difila)
                            val adapter =  BookAdapter(context!!, R.layout.list_view_prenotazioni, totalbucompact, totalfirmcompact, durate)

                            view.itemslistView.adapter = adapter

                            view.itemslistView.setOnItemClickListener{ parent: AdapterView<*>?, _: View?, position: Int, _: Long ->
                                val bundle = Bundle()
                                bundle.putSerializable("firm", totalfirmcompact[position])
                                bundle.putSerializable("book", parent!!.getItemAtPosition(position) as Booking)
                                bundle.putLong("durata", durate[position])

                                val intent = Intent(context!!, InfoTicket::class.java)
                                intent.putExtra("parameter", bundle)
                                startActivity(intent)
                            }
                        }
                    }
                })
            }
        })
        return view
    }
}