package com.example.queuefree

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.list_view_firm_prenotazioni.view.*

class BookFirmAdapter(
    customContext: Context,
    private val resource: Int,
    private val objects: ArrayList<Long>,
    private val firm: Firm): ArrayAdapter<Long>(customContext, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater= LayoutInflater.from(context)
        val customView= inflater.inflate(resource,parent,false)

        val hour = completeTimeStamp(firm.startHour+position,firm.startMinute)+" - "+completeTimeStamp(firm.startHour+position+1,firm.startMinute)
        val persone = objects[position].toString()+" su "+firm.capienza

        customView.personePrenotate.text = persone
        customView.orarioPrentFirm.text = hour

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