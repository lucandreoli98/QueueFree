package com.example.queuefree

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FirebaseDatabaseHelper () {

    var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
    var referenceuser: DatabaseReference = database.getReference("users")
    var referencefirm: DatabaseReference = database.getReference("firm")
    var referenceBooking: DatabaseReference = database.getReference("bookings")
    var user = User()
    var firm= Firm()
    var bookings: ArrayList<Booking> = ArrayList()
    var bookingsFirm: ArrayList<Long> = ArrayList()

    interface DataStatus {
        fun DataIsLoaded(user: User)
    }
    interface DataStatusFirm {
        fun DataisLoadedFirm(firm:Firm)
    }
    interface DataStatusBooking {
        fun BookingisLoaded(bookings: ArrayList<Long>)
    }

    fun readUserFromDB(ds: DataStatus){
        referenceuser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                    for (uDB in p0.children) { // per tutti gli utente dentro la tabella user
                        if(id == uDB.key){
                            for(field in uDB.children) {
                                when(field.key) {
                                    "nome" -> user.nome = field.value as String
                                    "cognome" -> user.cognome = field.value as String
                                    "email" -> user.email = field.value as String
                                    "dd" -> user.dd = field.value as Long
                                    "mm" -> user.mm = field.value as Long
                                    "yy" -> user.yy = field.value as Long
                                }
                            }
                            ds.DataIsLoaded(user)
                        }
                    }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("OnCancelled", p0.toException().toString())
            }
        })
    }

    fun readFirmsandtakeAdress(ds: DataStatusFirm, cat: String) {
        referencefirm.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("OnCancelled", p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                    for(aziende in p0.children){
                        val fid=aziende.key
                        if(p0.child("$fid/categoria").value?.equals(cat.trim())!!) {
                            val f=Firm()
                            for(field in aziende.children){
                                when(field.key){
                                    "id" -> f.id = field.value as String
                                    "nomeazienda" -> f.nomeazienza = field.value as String
                                    "email" -> f.email = field.value as String
                                    "password" -> f.password = field.value as String
                                    "categoria" -> f.categoria= field.value as String
                                    "location" -> f.location = field.value as String
                                    "startHour"->f.startHour= field.value as Long
                                    "endHour"->f.endHour= field.value as Long
                                    "endMinute"->f.endMinute= field.value as Long
                                    "startMinute"->f.startMinute= field.value as Long
                                    "capienza"->f.capienza= field.value as Long
                                    "descrizione"->f.descrizione= field.value as String
                                    "maxTurn"->firm.maxTurn=field.value as Long
                                }
                            }
                            ds.DataisLoadedFirm(f)
                        }
                    }
            }
        })
    }

    fun readFirmsfromEmail(ema: String,ds: DataStatusFirm) {
        referencefirm.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("OnCancelled", p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                    for(aziende in p0.children){
                        val fid=aziende.key
                        if(p0.child("$fid/email").value?.equals(ema.trim())!!) {
                            val f=Firm()
                            for(field in aziende.children){
                                when(field.key){
                                    "id" -> f.id = field.value as String
                                    "nomeazienza" -> f.nomeazienza = field.value as String
                                    "email" -> f.email = field.value as String
                                    "password" -> f.password = field.value as String
                                    "categoria" -> f.categoria= field.value as String
                                    "location" -> f.location = field.value as String
                                    "startHour"->f.startHour= field.value as Long
                                    "endHour"->f.endHour= field.value as Long
                                    "endMinute"->f.endMinute= field.value as Long
                                    "startMinute"->f.startMinute= field.value as Long
                                    "capienza"->f.capienza= field.value as Long
                                    "descrizione"->f.descrizione= field.value as String
                                    "maxTurn"->firm.maxTurn=field.value as Long
                                }
                            }
                            ds.DataisLoadedFirm(f)
                        }
                    }
            }
        })
    }

    fun readFirmFromDB(ds:DataStatusFirm){
        referencefirm.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                    for (uDB in p0.children) { // per tutti gli utente dentro la tabella user
                        if(id == uDB.key){
                            for(field in uDB.children) {
                                when(field.key) {

                                    "nomeazienda" -> firm.nomeazienza = field.value as String
                                    "email" -> firm.email = field.value as String
                                    "password" -> firm.password = field.value as String
                                    "categoria" -> firm.categoria= field.value as String
                                    "location" -> firm.location = field.value as String
                                    "startHour"->firm.startHour= field.value as Long
                                    "endHour"->firm.endHour= field.value as Long
                                    "endMinute"->firm.endMinute= field.value as Long
                                    "startMinute"->firm.startMinute= field.value as Long
                                    "capienza"->firm.capienza= field.value as Long
                                    "descrizione"->firm.descrizione= field.value as String
                                    "maxTurn"->firm.maxTurn=field.value as Long

                                }
                            }
                            ds.DataisLoadedFirm(firm)
                        }
                    }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("OnCancelled", p0.toException().toString())
            }
        })

    }

    // Lettura delle prenotazioni in base alla mail dell'azienda
    fun readBookingFromEmail(emailFirm: String, ds: DataStatusBooking){
        bookings.clear()
        referenceBooking.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(booking in snapshot.children){
                        if(emailFirm == booking.key){
                            bookings.add(Booking())
                            for(book in booking.children){
                                when(book.key){
                                    "dd" -> bookings[bookings.size-1].dd = book.value as Long
                                    "mm" -> bookings[bookings.size-1].mm = book.value as Long
                                    "yy" -> bookings[bookings.size-1].yy = book.value as Long
                                    "nOre" -> bookings[bookings.size-1].nOre = book.value as Long
                                    "nPartecipanti" -> bookings[bookings.size-1].nPartecipanti = book.value as Long
                                }
                            }
                        }
                    }
                }
                //ds.BookingisLoaded(bookings)
            }

        })
    }

    fun readDailyBooking(dd: Long, mm: Long, yy: Long, firm: Firm, ds: DataStatusBooking){
        referenceBooking.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                bookingsFirm.clear()

                if((firm.endHour - firm.startHour)>0)
                    for(i in 0 until (firm.endHour - firm.startHour))
                        bookingsFirm.add(firm.capienza)
                else
                    for(i in 0..23)
                        bookingsFirm.add(firm.capienza)

                if(snapshot.exists()){
                    for(firms in snapshot.children){
                        if(firm.id == firms.key){ // se c'è l'id dell'azienda
                            for(book in firms.children){
                                val booking = Booking()
                                for(data in book.children){
                                    when(data.key) {
                                        "dd" -> booking.dd = data.value as Long
                                        "mm" -> booking.mm = data.value as Long
                                        "yy" -> booking.yy = data.value as Long
                                        "nore" -> booking.nOre = data.value as Long
                                        "npartecipanti" -> booking.nPartecipanti = data.value as Long
                                    }
                                }
                                Log.d("PRENOTAZIONI: ", "${booking.dd} ${booking.mm} ${booking.yy} ${booking.nPartecipanti}")
                                if(booking.dd == dd && booking.mm == mm && booking.yy == yy)
                                    bookingsFirm[booking.nOre.toInt()] -=  booking.nPartecipanti
                            }
                        }
                    }
                }
                ds.BookingisLoaded(bookingsFirm)
            }

        })
    }
}