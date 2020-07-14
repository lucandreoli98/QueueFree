package com.example.queuefree

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FinishTurnReminder : Service() {

    val database=FirebaseDatabaseHelper()
    private val CHANNEL_ID2="notificationChannel2"


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startTimer()

    }

    fun startTimer() {

        val task: TimerTask = object : TimerTask() {
            override fun run() {
                val calendar = Calendar.getInstance()
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH)+1
                val year = calendar.get(Calendar.YEAR)
                val hours=calendar.get(Calendar.HOUR_OF_DAY)
                val minute=calendar.get(Calendar.MINUTE)
                Log.d("ORA","$hours")

                val date=Calendar.getInstance()
                date.set(year,month,day,hours,minute+1,0)
                calendar.set(year,month,day,hours,minute,0)


                Log.d("TIMER","IL timer si avvierà alle ${calendar.time} in millisecondi ${calendar.timeInMillis} mentre quella attuale è ${Date().time}")
                startTimer1(date.timeInMillis-calendar.timeInMillis,"Ciao")



                database.readAllFirmFromDB(object : FirebaseDatabaseHelper.DataStatusHashFirm {
                    override fun dataisLoadedFirm(firms: HashMap<String, Firm>) {
                        database.readBookingUser(
                            firms,
                            object : FirebaseDatabaseHelper.DataBookingUser {
                                override fun BookingUserisLoaded(bookingUser: ArrayList<BookingUser>) {
                                    val totalbu = java.util.ArrayList<Booking>()
                                    val totalFirm = java.util.ArrayList<Firm>()

                                    for (bu in bookingUser)
                                        for (b in bu.bookings) {
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
                                    val totalbucompact = java.util.ArrayList<Booking>()
                                    val totalfirmcompact = java.util.ArrayList<Firm>()
                                    val durate = java.util.ArrayList<Long>()

                                    for (i in 0 until totalbu.size) {
                                        if (totalbu[i].dd != startd || totalbu[i].mm != startm || totalbu[i].yy != starty || totalbu[i].nPartecipanti != startParte || startfirm != totalFirm[i].nomeazienza || totalbu[i].nOre != (count + 1)) {
                                            startd = totalbu[i].dd
                                            startm = totalbu[i].mm
                                            starty = totalbu[i].yy
                                            startParte = totalbu[i].nPartecipanti
                                            startfirm = totalFirm[i].nomeazienza
                                            totalbucompact.add(totalbu[i])
                                            totalfirmcompact.add(totalFirm[i])
                                            if (difila != 0L)
                                                durate.add(difila)

                                            difila = 1
                                        } else
                                            difila++

                                        count = totalbu[i].nOre
                                    }
                                    durate.add(difila)



                                     for (j in 0 until totalbucompact.size) {
                                        if (totalbucompact[j].dd == day.toLong()) {
                                            if (totalbucompact[j].mm == month.toLong()) {
                                                if (totalbucompact[j].yy == year.toLong()) {
                                                    val starthour = totalfirmcompact[j].startHour + totalbucompact[j].nOre
                                                    val finishHour = starthour + durate[j]
                                                    if (finishHour>=hours){
                                                        val data :Calendar= Calendar.getInstance()
                                                        data.set(year,month,day,finishHour.toInt(),totalfirmcompact[j].startMinute.toInt(),0)
                                                        Log.d("TIMER","IL timer si avvierà alle ${data.time} di ${totalfirmcompact[j].nomeazienza}")
                                                        startTimer1(date.timeInMillis-calendar.timeInMillis,totalfirmcompact[j].nomeazienza)
                                                        var scompact=ArrayList<Booking>()
                                                        if (durate[j]>1){
                                                            scompact=scompatta(totalbucompact[j],durate[j])
                                                            database.CancelBookings(scompact,totalFirm[j])
                                                        }
                                                        else{
                                                            scompact.add(totalbu[j])
                                                            database.CancelBookings(scompact,totalFirm[j])
                                                        }



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

        val timer = Timer(true)
        val delay = 1000 // 1 minuto e mezzo

        val interval = 1000*60*60 // 2 hour

        timer.schedule(task,delay.toLong(),interval.toLong())



    }



    private fun startTimer1(date: Long,name:String) {


   /* val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent =Intent(this,AlarmReceiver::class.java)
        val pendinintent =PendingIntent.getBroadcast(this,0,intent,0)
        Log.d("ALARM","Ci arrivo qua?")
        alarmManager.setRepeating(AlarmManager.RTC,date,0,pendinintent)
        Log.d("ALARM2","Ci arrivo qua?")*/
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                sendNotification(name)

            }
        }
        val timer = Timer(false)
        timer.schedule(task,date)






    }

    private fun sendNotification(name: String) {
        // create the intent for the notification
        val notificationIntent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        // create the pending intent
        val flags = PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, flags)

        // create the variables for the notification
        val icon: Int = R.drawable.ic_baseline_cancel_24
        val contentTitle :CharSequence= "La tua prenotazione a $name è finita"
        val contentText: CharSequence = " Apprestati ad uscire!"

        // create the notification and set its data
        val notification: Notification = NotificationCompat.Builder(this,CHANNEL_ID2)
            .setSmallIcon(icon)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // display the notification
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_ID = 7
        manager.notify(NOTIFICATION_ID, notification)
    }

  fun  scompatta(booking:Booking,durata :Long ) :ArrayList<Booking>{
      var scompact=ArrayList<Booking>()
      for (i in 0 until durata ){
          var book=Booking(booking.dd,booking.mm,booking.yy,booking.nOre+durata,booking.nPartecipanti)
          scompact.add(book)
      }

      return scompact

  }





}









