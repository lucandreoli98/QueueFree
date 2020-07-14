package com.example.queuefree

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FirebaseDatabaseHelper{

    private var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
    private var referenceuser: DatabaseReference = database.getReference("users")
    private var referencefirm: DatabaseReference = database.getReference("firm")
    private var referenceBooking: DatabaseReference = database.getReference("bookings")
    private var user = User()

    interface DataStatus {
        fun DataIsLoaded(user: User)
    }
    interface DataStatusFirm {
        fun DataisLoadedFirm(firm:Firm)
    }
    interface DataStatusHashFirm {
        fun dataisLoadedFirm(firms: HashMap<String,Firm>)
    }
    interface DataStatusBooking {
        fun BookingisLoaded(nHour: ArrayList<Long>, bookings: ArrayList<Long>)
    }
    interface DataBookingUser {
        fun BookingUserisLoaded(bookingUser: ArrayList<BookingUser>)
    }
    interface DataBookingFirm {
        fun bookingFirmisLoaded(bookings: ArrayList<Booking>, bookingsHour: ArrayList<Long>)
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

    fun readFirmsByCategory(ds: DataStatusFirm, cat: String) {
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
                                    "maxTurn"->f.maxTurn=field.value as Long
                                    "maxPartecipants"->f.maxPartecipants=field.value as Long
                                    "giorni" -> f.giorni = field.value as String
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
                                    "maxTurn"->f.maxTurn=field.value as Long
                                    "maxPartecipants"->f.maxPartecipants=field.value as Long
                                    "giorni" -> f.giorni = field.value as String
                                }
                            }
                            ds.DataisLoadedFirm(f)
                        }
                    }
            }
        })
    }

    fun readFirmFromDB(ds: DataStatusFirm){
        referencefirm.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                    for (uDB in p0.children) { // per tutti gli utente dentro la tabella user
                        if(id == uDB.key){
                            val f=Firm()
                            for(field in uDB.children) {
                                when(field.key) {
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
                                    "maxTurn"->f.maxTurn=field.value as Long
                                    "maxPartecipants"->f.maxPartecipants=field.value as Long
                                    "giorni" -> f.giorni = field.value as String
                                }
                            }
                            ds.DataisLoadedFirm(f)
                        }
                    }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("OnCancelled", p0.toException().toString())
            }
        })

    }

    fun readDailyBooking(dd: Long, mm: Long, yy: Long, firm: Firm, ds: DataStatusBooking){


        referenceBooking.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookingsFirm = ArrayList<Long>()
                bookingsFirm.clear()

                if((firm.endHour - firm.startHour)>0)
                    for(i in 0 until (firm.endHour - firm.startHour))
                        bookingsFirm.add(firm.capienza)
                else
                    for(i in 0..23)
                        bookingsFirm.add(firm.capienza)

                val nHour = ArrayList<Long>()
                nHour.clear()
                if(snapshot.exists()) {
                    for (firms in snapshot.children) {
                        if (firm.id == firms.key) { // se c'è l'id dell'azienda
                            for (users in firms.children) {
                                val userBook = users.key.toString()
                                if(id == userBook.substring(0,28) && yy == userBook.substring(29,33).toLong() &&
                                   mm == userBook.substring(33,35).toLong() && dd == userBook.substring(35,37).toLong()){
                                    nHour.add(userBook.substring(38,40).toLong())
                                }
                            }
                        }
                    }
                }

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
                                if(booking.dd == dd && booking.mm == mm && booking.yy == yy)
                                    bookingsFirm[booking.nOre.toInt()] -=  booking.nPartecipanti
                            }
                        }
                    }
                }
                ds.BookingisLoaded(nHour, bookingsFirm)
            }

        })
    }

    fun readAllFirmFromDB(ds: DataStatusHashFirm){
        referencefirm.addValueEventListener(object : ValueEventListener {
            val f = HashMap<String,Firm>()
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    for(firms in p0.children) {
                        f[firms.key!!] = Firm()
                        for(field in firms.children){
                            when(field.key) {
                                "id" -> f[firms.key!!]!!.id = field.value as String
                                "nomeazienza" -> f[firms.key!!]!!.nomeazienza = field.value as String
                                "email" -> f[firms.key!!]!!.email = field.value as String
                                "password" -> f[firms.key!!]!!.password = field.value as String
                                "categoria" -> f[firms.key!!]!!.categoria= field.value as String
                                "location" -> f[firms.key!!]!!.location = field.value as String
                                "startHour"->f[firms.key!!]!!.startHour= field.value as Long
                                "endHour"->f[firms.key!!]!!.endHour= field.value as Long
                                "endMinute"->f[firms.key!!]!!.endMinute= field.value as Long
                                "startMinute"->f[firms.key!!]!!.startMinute= field.value as Long
                                "capienza"->f[firms.key!!]!!.capienza= field.value as Long
                                "descrizione"->f[firms.key!!]!!.descrizione= field.value as String
                                "maxTurn"->f[firms.key!!]!!.maxTurn=field.value as Long
                                "maxPartecipants"->f[firms.key!!]!!.maxPartecipants=field.value as Long
                                "giorni" -> f[firms.key!!]!!.giorni = field.value as String
                            }
                        }
                    }
                    ds.dataisLoadedFirm(f)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("OnCancelled", p0.toException().toString())
            }
        })

    }

    // Lettura delle prenotazioni in base alla mail dell'azienda
    fun readBookingUser(firms: HashMap<String,Firm>, ds: DataBookingUser){
        val bookingUser = ArrayList<BookingUser>() // tutte le prenotazioni effettuate
        bookingUser.clear()

        referenceBooking.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(firmID in snapshot.children){ // in questo punto id azienda
                        val booking = ArrayList<Booking>() // prenotazioni per la singola azienda
                        booking.clear()
                        for(book in firmID.children){
                            val idRead=book.key!!.split("-")
                            if (idRead[0].trim() == id) { // in questo punto hai la prenotazione nell'azienda
                                // dati azienda
                                booking.add(Booking())
                                for (field in book.children) {
                                    when (field.key) {
                                        "dd" -> booking[booking.size-1].dd = field.value as Long
                                        "mm" -> booking[booking.size-1].mm = field.value as Long
                                        "yy" -> booking[booking.size-1].yy = field.value as Long
                                        "nore" -> booking[booking.size-1].nOre = field.value as Long
                                        "npartecipanti" -> booking[booking.size-1].nPartecipanti = field.value as Long
                                    }
                                }
                            }
                        }
                        if(booking.size > 0) {
                            bookingUser.add(BookingUser(firms[firmID.key]!!, booking))
                        }
                    }
                    ds.BookingUserisLoaded(bookingUser)
                    referenceBooking.removeEventListener(this)
                }
            }
        })
    }

    fun readBookingFirm(day: Long, month: Long, year: Long, firm: Firm, ds: DataBookingFirm){
        var bookingsHour = ArrayList<Long>()
        var bookings = ArrayList<Booking>()

        if((firm.endHour - firm.startHour)>0)
            for(i in 0 until (firm.endHour - firm.startHour))
                bookingsHour.add(0)
        else
            for(i in 0..23)
                bookingsHour.add(firm.capienza)

        referenceBooking.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(firms in snapshot.children){ // in questo punto id azienda
                        if(id == firms.key){
                            for(userBooks in firms.children){
                                val ora = userBooks.key.toString().substring(38,40).toInt()
                                bookings.add(Booking())
                                for(field in userBooks.children){
                                    when (field.key) {
                                        "dd" -> bookings[bookings.size-1].dd = field.value as Long
                                        "mm" -> bookings[bookings.size-1].mm = field.value as Long
                                        "yy" -> bookings[bookings.size-1].yy = field.value as Long
                                        "nore" -> bookings[bookings.size-1].nOre = field.value as Long
                                        "npartecipanti" -> bookings[bookings.size-1].nPartecipanti = field.value as Long
                                    }
                                }
                                bookingsHour[ora] += bookings[bookings.size-1].nPartecipanti
                            }
                        }
                    }
                    ds.bookingFirmisLoaded(bookings,bookingsHour)
                }
            }
        })
    }

    fun CancelBookings(bookings: ArrayList<Booking>, firm: Firm){
        referenceBooking.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (azienda in snapshot.children){
                        if (azienda.key==firm.id){
                            for (prenotazione in azienda.children){
                                var str=prenotazione.key!!.split("-")
                                if (str[0]==id){
                                    var actualbook=Booking()
                                    for (field in prenotazione.children){
                                        when(field.key){
                                            "dd" -> actualbook.dd = field.value as Long
                                            "mm" -> actualbook.mm = field.value as Long
                                            "yy" -> actualbook.yy = field.value as Long
                                            "nore" -> actualbook.nOre = field.value as Long
                                            "npartecipanti" -> actualbook.nPartecipanti = field.value as Long

                                        }
                                    }
                                    Log.d("CANCELLAZIONE","bookings/${firm.id}/$id-${actualbook.yy}${isZero(actualbook.mm.toInt())}${actualbook.mm}${isZero(actualbook.dd.toInt())}${actualbook.dd}-${isZero(actualbook.nOre.toInt())}${actualbook.nOre}")

                                    for (i in 0 until bookings.size){
                                        Log.d("CONFRONTO","actual book: ${actualbook.yy}${isZero(actualbook.mm.toInt())}${actualbook.mm}${isZero(actualbook.dd.toInt())}${actualbook.dd}-${isZero(actualbook.nOre.toInt())}${actualbook.nOre} con bookings :${bookings[i].yy}${isZero(bookings[i].mm.toInt())}${bookings[i].mm}${isZero(bookings[i].dd.toInt())}${bookings[i].dd}-${isZero(bookings[i].nOre.toInt())}${bookings[i].nOre}")
                                        if (bookings[i].dd==actualbook.dd){
                                            if (bookings[i].mm==actualbook.mm)
                                                if(bookings[i].yy==actualbook.yy)
                                                    if (bookings[i].nOre==actualbook.nOre)
                                                        if (bookings[i].nPartecipanti==actualbook.nPartecipanti){
                                                             database.getReference("bookings/${firm.id}/$id-${actualbook.yy}${isZero(actualbook.mm.toInt())}${actualbook.mm}${isZero(actualbook.dd.toInt())}${actualbook.dd}-${isZero(actualbook.nOre.toInt())}${actualbook.nOre}").removeValue()
                                                                Log.d("ELIMINAZIONE","eliminato")

                                                            }


                                        }
                                    }
                                }


                            }

                        }
                    }
                }
            }

        })

    }

    private fun isZero(i: Int): String{
        return if(i < 10)
            "0"
        else
            ""
    }
}