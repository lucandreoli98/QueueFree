package com.example.queuefree

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*
import kotlin.collections.HashMap

class ReminderBookings : Service() {

    private val database = FirebaseDatabaseHelper()
    private val CHANNEL_ID1="i.apps.notifications"
    private val CHANNEL_DESCRIPTION="Descrizione"
    private var notificationManager: NotificationManager? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @Override
    override fun onCreate() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.notification_channel_start)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID1,CHANNEL_DESCRIPTION, importance)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager!!.createNotificationChannel(mChannel)


        }
        startTimer()
    }

    private fun startTimer() {
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                val calendar=Calendar.getInstance()
                val day=calendar.get(Calendar.DAY_OF_MONTH)
                val month=calendar.get(Calendar.MONTH)+1
                val year=calendar.get(Calendar.YEAR)
                val hour=calendar.get(Calendar.HOUR_OF_DAY)
                val minute=calendar.get(Calendar.MINUTE)
                calendar.set(year,month,day,hour,minute,0)
                var i=0
                Log.d("CALENDAR","${calendar.time}")

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
                                                val data=Calendar.getInstance()
                                                data.set(year,month,day,starthour.toInt(),totalfirmcompact[j].startMinute.toInt(),0)
                                                Log.d("DATA","${data.time} contro ${calendar.time}")
                                                Log.d("DATA"," "+data.after(calendar)+"  ${totalfirmcompact[j].nomeazienza}")
                                                if(data.after(calendar)){
                                                    var date=completeTimeStamp(starthour,totalfirmcompact[j].startMinute)
                                                    sendNotification("${totalfirmcompact[j].nomeazienza}: la prenotazione inizia alle $date",i)
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

        var timer = Timer(false)
        val delay = 1000 * 10 // 10 seconds

        val interval = 1000*60*30// 1/2 hour

        timer.schedule(task, delay.toLong(), interval.toLong())
    }

    private fun sendNotification(text: String, i:Int) {
        // create the intent for the notification
        val notificationIntent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        // create the pending intent
        val flags = PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, flags)

        // create the variables for the notification
        val icon: Int = R.drawable.ic_alarm_24
        val contentTitle = getText(R.string.notify)
        val contentText: CharSequence = text

        // create the notification and set its data
        val notification: Notification = NotificationCompat.Builder(this,CHANNEL_ID1)
            .setSmallIcon(icon)
            .setTicker("Ticker")
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // display the notification
        val NOTIFICATION_ID = i
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.notify(NOTIFICATION_ID, notification)
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