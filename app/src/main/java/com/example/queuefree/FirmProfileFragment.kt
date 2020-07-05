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

        fb.readFirmFromDB(object : FirebaseDatabaseHelper.DataStatusFirm {
            override fun DataisLoadedFirm(firm: Firm) {

            }

        })

        view.logout_firm.setOnClickListener{
            signOut()
        }



        return view
    }

    private fun signOut(){
        fireBase!!.signOut()

        LoginManager.getInstance().logOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

        val googleSignInClient = GoogleSignIn.getClient(context!!, gso)
        googleSignInClient.signOut()

        val i= Intent(context!!, MainActivity::class.java)
        i.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }
}