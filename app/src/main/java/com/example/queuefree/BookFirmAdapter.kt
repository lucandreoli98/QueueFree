package com.example.queuefree

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.list_view_prenotazioni.view.*

class BookFirmAdapter(
    private val customContext: Context,
    private val resource: Int,
    objects: ArrayList<Long>): ArrayAdapter<Long>(customContext, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater= LayoutInflater.from(context)
        val customView= inflater.inflate(resource,parent,false)

        /*customView.firmNameTicket.text = firm[position].nomeazienza
        customView.dataTicket.text = "${getItem(position)!!.dd}/${getItem(position)!!.mm}/${getItem(position)!!.yy}"
        customView.orarioTicket.text = "${completeTimeStamp(firm[position].startHour+getItem(position)!!.nOre,firm[position].startMinute)} - ${completeTimeStamp(durate[position]+firm[position].startHour+getItem(position)!!.nOre,firm[position].startMinute)}"
        customView.partecipantiTicket.text = getItem(position)!!.nPartecipanti.toString()

        when(firm[position].categoria){
            "Spiaggia"   -> customView.imageCategoryTicket.setImageResource(R.drawable.spiaggia)
            "Museo"      -> customView.imageCategoryTicket.setImageResource(R.drawable.museo_icon)
            "Biblioteca" -> customView.imageCategoryTicket.setImageResource(R.drawable.biblioteca)
        }
         */
        return customView
    }


}