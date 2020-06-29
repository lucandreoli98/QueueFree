package com.example.queuefree

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_show_profile.*

class ShowProfileFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_show_profile, container, false)

        val fb : FirebaseDatabaseHelper = FirebaseDatabaseHelper()

        fb!!.readUserFromDB(object : FirebaseDatabaseHelper.DataStatus {
            override fun DataIsLoaded(user: User) {
                nameSurnameText.setText("${user.nome} ${user.cognome}")
                emailTextView.setText(user.email)
                dataTextView.setText("${user.dd}/${user.mm}/${user.yy}")
            }

        })

        return view
    }
}