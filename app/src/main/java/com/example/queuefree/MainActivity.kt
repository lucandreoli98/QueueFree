package com.example.queuefree

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_main)

        //QueueFreeApp().notificationchannel1()
        //QueueFreeApp().notificationchannel2()


        supportFragmentManager.beginTransaction().replace(R.id.login_fragment, LoginFragment()).commit()
    }




}