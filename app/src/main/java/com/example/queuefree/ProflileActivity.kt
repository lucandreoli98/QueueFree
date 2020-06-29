package com.example.queuefree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ProflileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportFragmentManager.beginTransaction().replace(R.id.profileContainer, ProfileFragment()).commit()
    }
}
