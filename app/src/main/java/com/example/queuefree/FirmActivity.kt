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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firm)


        supportFragmentManager.beginTransaction().replace(R.id.fragment_firm, DashboardFragment()).commit()


    }

    override fun onBackPressed() {
        val intent = Intent(this,FirmActivity::class.java)
        startActivity(intent)
    }
}