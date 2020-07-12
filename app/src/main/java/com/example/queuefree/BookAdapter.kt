package com.example.queuefree

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_letsbook.view.*
import kotlinx.android.synthetic.main.list_view_prenotazioni.view.*

class BookAdapter(context: Context, resource: Int, objects: ArrayList<Booking>, private val firm: ArrayList<Firm>) : ArrayAdapter<Booking>(context, resource, objects) {
    val mcontext=context
    val mresources=resource




    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // contiene Firm e ArrayList di Booking
        Log.e("aAaaaaaaaaaaaaaaaaaaaa",getItem(position)!!.dd.toString())
        val data="${getItem(position)!!.dd}/${getItem(position)!!.mm}/${getItem(position)!!.yy}"
        var nore=getItem(position)!!.nOre
        val part="Partecipanti: ${getItem(position)!!.nPartecipanti}"
        val name = firm[position].nomeazienza
        //Toast.makeText(mcontext, firm.nomeazienza,Toast.LENGTH_LONG).show()
        var book=Booking(getItem(position)!!.dd,getItem(position)!!.mm,getItem(position)!!.yy,getItem(position)!!.nOre,getItem(position)!!.nPartecipanti)
        val inflater= LayoutInflater.from(mcontext)
        val convertView= inflater.inflate(mresources,parent,false)
        convertView.data_ora.text = data
        convertView.partecip.text = part
        convertView.name_firm.text = name


        return convertView
    }
}