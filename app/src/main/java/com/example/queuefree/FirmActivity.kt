package com.example.queuefree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FirmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firm)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_firm, FirmProfileFragment()).commit()
    }
}