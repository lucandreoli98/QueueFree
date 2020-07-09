package com.example.queuefree

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.prenotazioni_utente.view.*

class PrenotazioniFragment: Fragment() {
    val database=FirebaseDatabaseHelper()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.prenotazioni_utente, container, false)
        val resources=R.layout.list_view_prenotazioni
        database.readBookingUser(FirebaseAuth.getInstance().currentUser!!.uid.trim(),object: FirebaseDatabaseHelper.DataBookingUser{
            override fun BookingUserisLoaded(books: ArrayList<Booking>) {
                Toast.makeText(context,books[1].nPartecipanti.toString(),Toast.LENGTH_SHORT).show()
                val adapter= context?.let { BookAdapter(it,R.layout.list_view_prenotazioni,books) }
                view.itemslistView.setAdapter(adapter)
               // view.prenotazioni_mie.setText(database.bookings.get(0).nPartecipanti.toString())

            }

        })






        return view
    }
}