package com.example.queuefree

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class FirmActivity : AppCompatActivity() {

    private var fireBase: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.firm_dashboard)
        val btnset=findViewById<ImageButton>(R.id.settings)
        val btnbbok=findViewById<ImageButton>(R.id.books)
        val btnlogout=findViewById<Button>(R.id.logout)

        fireBase = FirebaseAuth.getInstance()

        btnset.setOnClickListener {
            setContentView(R.layout.activity_firm)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_firm, FirmProfileFragment()).commit()

        }

        btnlogout.setOnClickListener {
            fireBase!!.signOut()

            LoginManager.getInstance().logOut()

            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()

            val i= Intent(this, MainActivity::class.java)
            i.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }



    }
}