package com.example.queuefree

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.spinner_item.view.*
import kotlin.math.min

class SpinnerAdapter(ctx: Context, private val hours: List<Long>,
                     private val booking: List<Long>, private val nHour: ArrayList<Long>,
                     private val nPartecipanti: Int,
                     private val minute: Long) :
    ArrayAdapter<Long>(ctx, 0, hours) {


    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }
    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {

        val view = recycledView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)

        //Log.d("BOOKING", booking.toString()+" "+hours.toString())

        for(i in booking.indices){
            if(position == i){
                if(nHour.contains(i.toLong())){
                    view.prova.setBackgroundColor(Color.YELLOW)
                }
                else if(booking[i] >= nPartecipanti)
                    view.prova.setBackgroundColor(Color.GREEN)
                view.prova.text = completeTimeStamp(hours[i], minute)
            }
        }

        return view
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