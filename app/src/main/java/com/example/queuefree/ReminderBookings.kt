package com.example.queuefree

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import java.util.*
import kotlin.collections.HashMap

class ReminderBookings :Service() {

    private val database = FirebaseDatabaseHelper()
    private val CHANNEL_ID="101"





    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @Override
    override fun onCreate() {
        notificationchannel()


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

                                val totalbu = ArrayList<Booking>()
                                val totalFirm = ArrayList<Firm>()

                                for (bu in bookingUser)
                                    for(b in bu.bookings){
                                        totalbu.add(b)
                                        totalFirm.add(bu.firm)
                                    }

                                var startd = 0L
                                var startm = 0L
                                var starty = 0L
                                var count = 0L
                                var difila = 0L
                                var startParte = 0L
                                var startfirm = ""
                                val totalbucompact = ArrayList<Booking>()
                                val totalfirmcompact = ArrayList<Firm>()
                                val durate = ArrayList<Long>()

                                for(i in 0 until totalbu.size){
                                    if(totalbu[i].dd != startd || totalbu[i].mm != startm || totalbu[i].yy != starty || totalbu[i].nPartecipanti != startParte || startfirm != totalFirm[i].nomeazienza || totalbu[i].nOre != (count+1)){
                                        startd = totalbu[i].dd
                                        startm = totalbu[i].mm
                                        starty = totalbu[i].yy
                                        startParte = totalbu[i].nPartecipanti
                                        startfirm =  totalFirm[i].nomeazienza
                                        totalbucompact.add(totalbu[i])
                                        totalfirmcompact.add(totalFirm[i])
                                        if(difila!=0L)
                                            durate.add(difila)

                                        difila=1
                                    }else
                                        difila++

                                    count = totalbu[i].nOre
                                }
                                durate.add(difila)

                                for(j in 0 until totalbucompact.size){
                                    if(totalbucompact[j].dd==day.toLong()){
                                        if(totalbucompact[j].mm==month.toLong()){
                                            if(totalbucompact[j].yy==year.toLong()){
                                                val starthour= totalfirmcompact[j].startHour+totalbucompact[j].nOre
                                                Log.d("NOTIFICA","${totalfirmcompact[j].nomeazienza}: $starthour apre alle ${totalfirmcompact[j].startHour}")
                                                if(Date().hours<=starthour){
                                                    var date=completeTimeStamp(starthour,totalfirmcompact[j].startMinute)
                                                    sendNotification("${totalfirmcompact[j].nomeazienza}: la prenotazione inizia alle $date",i)
                                                    ++i
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

        val interval = 1000*60*60 // 1 hour

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

    fun notificationchannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.notification_channel_start)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}
