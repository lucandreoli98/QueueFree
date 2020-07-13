package com.example.queuefree

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_letsbook.view.*
import kotlinx.android.synthetic.main.list_view_prenotazioni.view.*

class BookAdapter(private val customContext: Context,
                  private val resource: Int,
                  objects: ArrayList<Booking>,
                  private val firm: ArrayList<Firm>,
                  private val durate: ArrayList<Long>) : ArrayAdapter<Booking>(customContext, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater= LayoutInflater.from(context)
        val customView= inflater.inflate(resource,parent,false)

        customView.firmNameTicket.text = firm[position].nomeazienza
        customView.dataTicket.text = "${getItem(position)!!.dd}/${getItem(position)!!.mm}/${getItem(position)!!.yy}"
        customView.orarioTicket.text = "${completeTimeStamp(firm[position].startHour+getItem(position)!!.nOre,firm[position].startMinute)} - ${completeTimeStamp(durate[position]+firm[position].startHour+getItem(position)!!.nOre,firm[position].startMinute)}"
        customView.partecipantiTicket.text = getItem(position)!!.nPartecipanti.toString()

        when(firm[position].categoria){
            "Spiaggia"   -> customView.imageCategoryTicket.setImageResource(R.drawable.spiaggia)
            "Museo"      -> customView.imageCategoryTicket.setImageResource(R.drawable.museo_icon)
            "Biblioteca" -> customView.imageCategoryTicket.setImageResource(R.drawable.biblioteca)
        }
        //var book=Booking(getItem(position)!!.dd,getItem(position)!!.mm,getItem(position)!!.yy,getItem(position)!!.nOre,getItem(position)!!.nPartecipanti)

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