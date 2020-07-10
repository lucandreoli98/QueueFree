package com.example.queuefree

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_letsbook.view.*
import kotlinx.android.synthetic.main.list_view_prenotazioni.view.*

class BookAdapter(context: Context, resource: Int, objects: ArrayList<Booking>,private val firm:Firm) : ArrayAdapter<Booking>(context, resource, objects) {
    val mcontext=context
    val mresources=resource




    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // contiene Firm e ArrayList di Booking

            var data="${getItem(position)!!.dd}/${getItem(position)!!.mm}/${getItem(position)!!.yy}"
            var nore=getItem(position)!!.nOre
            var part="Partecipanti: ${getItem(position)!!.nPartecipanti}"
            var name = firm.nomeazienza
            Toast.makeText(mcontext, firm.nomeazienza,Toast.LENGTH_LONG).show()
            var book=Booking(getItem(position)!!.dd,getItem(position)!!.mm,getItem(position)!!.yy,getItem(position)!!.nOre,getItem(position)!!.nPartecipanti)
            var inflater=LayoutInflater.from(mcontext)
            val convertView=inflater.inflate(mresources,parent,false)
            convertView.data_ora.setText(data)
            convertView.partecip.setText(part)
            convertView.name_firm.setText(name)


        return convertView
    }
}