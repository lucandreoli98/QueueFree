package com.example.queuefree

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.*
import kotlin.collections.HashMap

class ReminderBookings :Service() {

    private val database = FirebaseDatabaseHelper()





    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @Override
    override fun onCreate() {
        startTimer()


    }



    private fun startTimer() {
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                val calendar=Calendar.getInstance()
                val day=calendar.get(Calendar.DAY_OF_MONTH)
                val month=calendar.get(Calendar.MONTH)+1
                val year=calendar.get(Calendar.YEAR)
                var i=1


                database.readAllFirmFromDB(object:FirebaseDatabaseHelper.DataStatusHashFirm{
                    override fun dataisLoadedFirm(firms: HashMap<String, Firm>) {
                        database.readBookingUser(firms,object:FirebaseDatabaseHelper.DataBookingUser{
                            override fun BookingUserisLoaded(bookingUser: ArrayList<BookingUser>) {
                                for (BookingsFirm in bookingUser){
                                    for(Bookings in BookingsFirm.bookings) {
                                        Log.d("NOTIFICA", "${BookingsFirm.firm.nomeazienza} : ${Bookings.dd}/${Bookings.mm}/${Bookings.yy}, current date : $day/$month/$year")
                                        if (Bookings.dd.toInt() == day) {
                                            if (Bookings.mm.toInt() == month){
                                                if (Bookings.yy.toInt() == year) {
                                                    val starthour = BookingsFirm.firm.startHour + Bookings.nOre
                                                     val time =completeTimeStamp(starthour,BookingsFirm.firm.startMinute)
                                                    sendNotification("${BookingsFirm.firm.nomeazienza}:la prenotazione inizia alle $time",i)
                                                    i++

                                                }
                                        }
                                    }

                                    }
                                }
                            }

                        })

                    }

                })





            }

        }

       var timer = Timer(true)
        val delay = 1000 * 10 // 10 seconds

        val interval = 1000 * 60  // 1 hour

        timer.schedule(task, delay.toLong(), interval.toLong())

    }


    private fun sendNotification(text: String, i:Int) {
        // create the intent for the notification
        val notificationIntent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        // create the pending intent
        val flags = PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, flags)

        // create the variables for the notification
        val icon: Int = R.drawable.ic_alarm_24
        val contentTitle = getText(R.string.notify)
        val contentText: CharSequence = text

        // create the notification and set its data
        val notification: Notification = Notification.Builder(this)
            .setSmallIcon(icon)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // display the notification
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_ID = i
        manager.notify(NOTIFICATION_ID, notification)
    }

    fun completeTimeStamp(hour :Long,minute: Long):String{
        return if(hour<10){
            if(minute<10){
                "0${hour}:0${minute}"
            } else {
                "0${hour}:${minute}"
            }
        } else if(minute<10){
            "${hour}:0${minute}"
        } else {
            "${hour}:${minute}"
        }
    }
}
