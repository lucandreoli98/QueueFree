package com.example.queuefree

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.spinner_item.view.*

class SpinnerAdapter(ctx: Context, hours: List<String>, disponibilita: List<Int>?) :
    ArrayAdapter<String>(ctx, 0, hours) {


    private val ct = ctx


    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }
    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {
        //val mood = getItem(position)
        val view = recycledView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)


        if(position==0) view.prova.setBackgroundColor(Color.BLACK)
        if(position==1) view.prova.setBackgroundColor(Color.GRAY)

        return view
    }
}