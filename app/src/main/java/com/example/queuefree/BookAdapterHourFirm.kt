package com.example.queuefree

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.list_view_prenotazioni.view.*

class BookAdapterHourFirm(
    customContext: Context,
    private val resource: Int,
    private val bookings: ArrayList<Booking>,
    private val firm: Firm,
    private val users: HashMap<String, User>,
    private val usersID: ArrayList<String>,
    private val ora: Int) : ArrayAdapter<Booking>(customContext, resource, bookings) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater= LayoutInflater.from(context)
        val customView= inflater.inflate(resource,parent,false)

        val nomeUser = users[usersID[position]]!!.nome + " " + users[usersID[position]]!!.cognome
        val data = bookings[position].dd.toString()+"/"+bookings[position].mm.toString()+"/"+bookings[position].yy.toString()
        val orario = completeTimeStamp(firm.startHour+ora,firm.startMinute)+" - "+completeTimeStamp(firm.startHour+ora+1,firm.startMinute)

        customView.firmNameTicket.text = nomeUser
        customView.dataTicket.text = data
        customView.orarioTicket.text = orario
        customView.partecipantiTicket.text = bookings[position].nPartecipanti.toString()

        when(firm.categoria){
            "Spiaggia"   -> customView.imageCategoryTicket.setImageResource(R.drawable.spiaggia)
            "Museo"      -> customView.imageCategoryTicket.setImageResource(R.drawable.museo_icon)
            "Biblioteca" -> customView.imageCategoryTicket.setImageResource(R.drawable.biblioteca)
        }

        return customView
    }

    private fun completeTimeStamp(hour :Long, minute: Long):String{
        return if(hour<10){
            if(minute<10){
                "0${hour}:0${minute}"
            } else {
                "0${hour}:${minute}"
            }
        } else if(minute<10){
            "${hour}:0${minute}"
        } else {
            "${hour}:${minute}"
        }
    }
}