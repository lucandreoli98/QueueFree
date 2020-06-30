package com.example.queuefree

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*
import kotlinx.android.synthetic.main.fragment_show_profile.*

class EditProfileFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        var nome = arguments?.getString("nome")
        var cognome = arguments?.getString("cognome")
        var email = arguments?.getString("email")
        var dd = arguments?.getLong("dd")
        var mm = arguments?.getLong("mm")
        var yy = arguments?.getString("yy")

        var nameEdit = view.nameSurnameEditText
        //var nameEdit = view.findViewById<EditText>(R.id.nameSurnameEditText)
        Log.d("NOME E COGNOME", "$nome")

        nameEdit!!.setText("$nome")
        emailEditText!!.text = email
        dataEditTextView!!.setText("$dd/$mm/$yy")

        return view
    }
}