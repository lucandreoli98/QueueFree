package com.example.queuefree

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.firm_dashboard.*
import kotlinx.android.synthetic.main.firm_dashboard.view.*

class DashboardFragment : Fragment() {


    private var fireBase: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.firm_dashboard, container, false)

        fireBase = FirebaseAuth.getInstance()

        view.settings.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.fragment_firm, FirmProfileFragment())
                .commit()
        }
            view.logout.setOnClickListener {
            fireBase!!.signOut()

            LoginManager.getInstance().logOut()

            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

            val googleSignInClient = context?.let { it1 -> GoogleSignIn.getClient(it1, gso) }
            googleSignInClient!!.signOut()

            val i= Intent(context   , MainActivity::class.java)
            i.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }




        return view
    }




}