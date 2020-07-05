package com.example.queuefree

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_firm_profile.view.*

class FirmProfileFragment: Fragment() {


    private var fireBase: FirebaseAuth? = null

    private val fb : FirebaseDatabaseHelper = FirebaseDatabaseHelper()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_firm_profile, container, false)

        fireBase = FirebaseAuth.getInstance()




        return view
    }
}