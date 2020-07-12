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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FinishTurnReminder : Service() {

    val database=FirebaseDatabaseHelper()


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val calendar= Calendar.getInstance()
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val month=calendar.get(Calendar.MONTH)+1
        val year=calendar.get(Calendar.YEAR)



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
                                            var isContinue=true
                                            database.readDailyBooking(Bookings.dd,Bookings.mm,Bookings.yy,BookingsFirm.firm,object :FirebaseDatabaseHelper.DataStatusBooking{
                                                override fun BookingisLoaded(nHour: ArrayList<Long>, bookings: ArrayList<Long>) {
                                                    var z=0
                                                    var i=0
                                                  while (i < BookingsFirm.firm.maxTurn-1){
                                                      if (nHour.contains(i.toLong())){
                                                          z=nHour[i].toInt()+1
                                                          while (isContinue){
                                                              if (nHour.contains((i+1).toLong())){
                                                                  i++
                                                              }
                                                              else{

                                                                  isContinue=false
                                                                  var finishtime=starthour+z
                                                                  val date=Date(Bookings.yy.toInt(),Bookings.mm.toInt(),Bookings.dd.toInt(),finishtime.toInt(),BookingsFirm.firm.startMinute.toInt())

                                                                  startTimer(date)

                                                              }


                                                          }
                                                      }

                                                  }

                                                }

                                            })



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


    private fun startTimer(date: Date) {
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                sendNotification("La prenotazione è finita, la preghiamo di uscire")


            }

        }

        var timer = Timer(true)
        timer.schedule(task, date)

    }









    private fun sendNotification(text: String) {
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
        val NOTIFICATION_ID = 5
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