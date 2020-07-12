package com.example.queuefree

import android.app.Application
import android.content.Intent

class QueueFreeApp :Application() {

override fun onCreate(){


    super.onCreate()


    // start service
    val service = Intent(this, ReminderBookings::class.java)
    startService(service)

}
}