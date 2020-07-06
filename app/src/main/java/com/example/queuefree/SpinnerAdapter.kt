package com.example.queuefree

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class MyAdapter(context: Context?, textViewResourceId: Int, objects: Array<String?>?) :
    ArrayAdapter<Any?>(context!!, textViewResourceId, objects!!) {
/*
    fun getCustomView(position: Int, convertView: View?, parent: ViewGroup?): View {

        // Inflating the layout for the custom Spinner
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(R.layou., parent, false)

// Declaring and Typecasting the textview in the inflated layout
        val tvLanguage = layout
            .findViewById(R.id.tvLanguage) as TextView

// Setting the text using the array
        tvLanguage.setText(Languages.get(position))

// Setting the color of the text
        tvLanguage.setTextColor(Color.rgb(75, 180, 225))

// Declaring and Typecasting the imageView in the inflated layout
        val img: ImageView = layout.findViewById(R.id.imgLanguage) as ImageView

// Setting an image using the id's in the array
        img.setImageResource(images.get(position))

// Setting Special atrributes for 1st element
        if (position == 0) {
// Removing the image view
            img.setVisibility(View.GONE)
            // Setting the size of the text
            tvLanguage.textSize = 20f
            // Setting the text Color
            tvLanguage.setTextColor(Color.BLACK)
        }
        return layout
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup?
    ): View {
        return getCustomView(position, convertView, parent)
    }

    // It gets a View that displays the data at the specified position
    fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getCustomView(position, convertView, parent)
    }*/
}