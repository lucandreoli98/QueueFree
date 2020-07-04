package com.example.queuefree

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class LetsbookActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_letsbook)

        supportFragmentManager.beginTransaction().replace(R.id.activity_letsbook, LetsbookFragment()).commit()

    }
}