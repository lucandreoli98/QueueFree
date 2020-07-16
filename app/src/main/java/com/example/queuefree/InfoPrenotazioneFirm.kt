package com.example.queuefree

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.prenotazioni_utente.*

class InfoPrenotazioneFirm: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prenotazioni_utente)
        prenNull.visibility = View.INVISIBLE

        val fb = FirebaseDatabaseHelper()
        fb.readAllUserFromDB(this, object: FirebaseDatabaseHelper.DataStatusHashUser{
            override fun dataisLoadedUser(mContext: Context, users: HashMap<String, User>) {
                itemslistView.visibility = View.VISIBLE

                val bundle = intent.getBundleExtra("parameter")

                val bookings= bundle!!.getSerializable("booking") as ArrayList<Booking>
                val dailyBooking = ArrayList<Booking>()
                val dailyUser = ArrayList<String>()
                val ora = bundle.getInt("ora")

                val usersID = bundle.getStringArrayList("usersID")
                for(i in 0 until bookings.size){
                    if(bookings[i].nOre == ora.toLong()){
                        dailyBooking.add(bookings[i])
                        dailyUser.add(usersID!![i])
                    }
                }

                val adapter =  BookAdapterHourFirm(mContext, R.layout.list_view_prenotazioni, dailyBooking,
                    bundle.getSerializable("firm") as Firm, users, dailyUser, ora)
                itemslistView.adapter = adapter
            }
        })
    }
}