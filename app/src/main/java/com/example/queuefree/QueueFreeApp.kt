package com.example.queuefree

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.util.Log

class QueueFreeApp :Application() {

    val CHANNEL_ID1="notificationChannel1"
    val CHANNEL_ID2="notificationChannel2"

override fun onCreate(){

    super.onCreate()
    notificationchannel1()
    Log.d("CHANNEL1","Canale creato")
    notificationchannel2()
    Log.d("CHANNEL2","Canale creato")
    // start service
    val service = Intent(this, ReminderBookings::class.java)
    startService(service)
    Log.d("INTENT1","Intent fatto")
    val service2 = Intent(this, FinishTurnReminder::class.java)
    startService(service2)
    Log.d("INTENT2","Intent fatto")

}


    fun notificationchannel1(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.notification_channel_start)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID1, name, importance)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    fun notificationchannel2(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.notification_channel_start)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID2, name, importance)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}