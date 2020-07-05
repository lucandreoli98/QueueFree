package com.example.queuefree

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LetsbookActivity: AppCompatActivity() {

    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letsbook)
        val b = Bundle()
        email = intent.getStringExtra("email")

        b.putString("email",email)

        val fragment = LetsbookFragment()
        fragment.arguments=b

        supportFragmentManager.beginTransaction().replace(R.id.activity_letsbook, fragment).commit()
    }
}