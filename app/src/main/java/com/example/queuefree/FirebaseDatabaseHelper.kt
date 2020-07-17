package com.example.queuefree

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FirebaseDatabaseHelper{

    private var database : FirebaseDatabase = FirebaseDatabase.getInstance()
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
        fun bookingFirmisLoaded(bookings: ArrayList<Booking>, bookingsHour: ArrayList<Long>,usersID: ArrayList<String>)
    }
    interface DataStatusHashUser{
        fun dataisLoadedUser(mContext: Context, users: HashMap<String,User>)
    }
    interface DataStatusCancel{
        fun isDeleted(mContext: Context)
    }
    interface DataAlreadyBookin{
        fun alreadyBooked(isAlreadyBooked: Boolean)
    }

    fun readUserFromDB(ds: DataStatus){
        val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
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
                            referenceuser.removeEventListener(this)
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
                                    "latitude" -> f.latitude = field.value as Double
                                    "longitude" -> f.longitude = field.value as Double
                                }
                            }
                            ds.DataisLoadedFirm(f)
                            referencefirm.removeEventListener(this)
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
                                    "latitude" -> f.latitude = field.value as Double
                                    "longitude" -> f.longitude = field.value as Double
                                }
                            }
                            ds.DataisLoadedFirm(f)
                            referencefirm.removeEventListener(this)
                        }
                    }
            }
        })
    }

    fun readFirmFromDB(ds: DataStatusFirm){
        val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
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
                                    "latitude" -> f.latitude = field.value as Double
                                    "longitude" -> f.longitude = field.value as Double
                                }
                            }
                            ds.DataisLoadedFirm(f)
                            referencefirm.removeEventListener(this)
                        }
                    }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("OnCancelled", p0.toException().toString())
            }
        })

    }

    fun readDailyBooking(dd: Long, mm: Long, yy: Long, firm: Firm, ds: DataStatusBooking){
        val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
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
                referenceBooking.removeEventListener(this)
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
                                "latitude" -> f[firms.key!!]!!.latitude = field.value as Double
                                "longitude" -> f[firms.key!!]!!.longitude = field.value as Double
                            }
                        }
                    }
                    ds.dataisLoadedFirm(f)
                    referencefirm.removeEventListener(this)
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

        referenceBooking.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
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
        val bookingsHour = ArrayList<Long>()
        val bookings = ArrayList<Booking>()
        val usersID = ArrayList<String>()

        if((firm.endHour - firm.startHour)>0)
            for(i in 0 until (firm.endHour - firm.startHour))
                bookingsHour.add(0)
        else
            for(i in 0..23)
                bookingsHour.add(firm.capienza)

        referenceBooking.addValueEventListener(object: ValueEventListener{
            val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(firms in snapshot.children){ // in questo punto id azienda
                        if(id == firms.key){
                            for(userBooks in firms.children){
                                if(year == userBooks.key!!.substring(29,33).toLong() && month == userBooks.key!!.substring(33,35).toLong() &&
                                    day == userBooks.key!!.substring(35,37).toLong()){

                                    usersID.add(userBooks.key.toString().substring(0,28).trim())
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
                    }
                    ds.bookingFirmisLoaded(bookings,bookingsHour,usersID)
                    referenceBooking.removeEventListener(this)
                }
            }
        })
    }

    fun cancelBookings(bookings: ArrayList<Booking>, firm: Firm){
        referenceBooking.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
                if (snapshot.exists()){
                    for (azienda in snapshot.children){
                        if (azienda.key==firm.id){
                            for (prenotazione in azienda.children){
                                val str=prenotazione.key!!.split("-")
                                if (str[0]==id){
                                    val actualbook=Booking()
                                    for (field in prenotazione.children){
                                        when(field.key){
                                            "dd" -> actualbook.dd = field.value as Long
                                            "mm" -> actualbook.mm = field.value as Long
                                            "yy" -> actualbook.yy = field.value as Long
                                            "nore" -> actualbook.nOre = field.value as Long
                                            "npartecipanti" -> actualbook.nPartecipanti = field.value as Long

                                        }
                                    }

                                    for (i in 0 until bookings.size){
                                        if (bookings[i].dd==actualbook.dd){
                                            if (bookings[i].mm==actualbook.mm)
                                                if(bookings[i].yy==actualbook.yy)
                                                    if (bookings[i].nOre==actualbook.nOre)
                                                        if (bookings[i].nPartecipanti==actualbook.nPartecipanti){
                                                             database.getReference("bookings/${firm.id}/$id-${actualbook.yy}${isZero(actualbook.mm.toInt())}${actualbook.mm}${isZero(actualbook.dd.toInt())}${actualbook.dd}-${isZero(actualbook.nOre.toInt())}${actualbook.nOre}").removeValue()
                                                                Log.d("ELIMINAZIONE","eliminata la prenotazione bookings/${firm.id}/$id-${actualbook.yy}${isZero(actualbook.mm.toInt())}${actualbook.mm}${isZero(actualbook.dd.toInt())}${actualbook.dd}-${isZero(actualbook.nOre.toInt())}${actualbook.nOre}")
                                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    referenceBooking.removeEventListener(this)
                }
            }
        })
    }

    fun readAllUserFromDB(mContext: Context, ds: DataStatusHashUser){
        referenceuser.addValueEventListener(object : ValueEventListener {
            val users = HashMap<String,User>()
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    for(user in p0.children) {
                        users[user.key!!] = User()
                        for(field in user.children){
                            when(field.key) {
                                "nome" -> users[user.key!!]!!.nome = field.value as String
                                "cognome" -> users[user.key!!]!!.cognome = field.value as String
                                "email" -> users[user.key!!]!!.email = field.value as String
                                "dd" -> users[user.key!!]!!.dd = field.value as Long
                                "mm" -> users[user.key!!]!!.mm= field.value as Long
                                "yy" -> users[user.key!!]!!.yy = field.value as Long
                            }
                        }
                    }
                    ds.dataisLoadedUser(mContext,users)
                    referenceuser.removeEventListener(this)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("OnCancelled", p0.toException().toString())
            }
        })
    }

    fun removeBook(mContext: Context,firm: Firm, booking: Booking,durata: Long, ds: DataStatusCancel){
        referenceBooking.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e("OnCancelled",error.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
                if (snapshot.exists()) {
                    for (azienda in snapshot.children) {
                        if (azienda.key == firm.id) {
                            for (prenotazione in azienda.children) {
                                if (prenotazione.key!!.split("-")[0] == id) {
                                    val actualbook = Booking()
                                    for (field in prenotazione.children) {
                                        when (field.key) {
                                            "dd" -> actualbook.dd = field.value as Long
                                            "mm" -> actualbook.mm = field.value as Long
                                            "yy" -> actualbook.yy = field.value as Long
                                            "nore" -> actualbook.nOre = field.value as Long
                                            "npartecipanti" -> actualbook.nPartecipanti = field.value as Long
                                        }
                                    }
                                    if(booking.yy == actualbook.yy)
                                        if(booking.mm == actualbook.mm)
                                            if(booking.dd == actualbook.dd)
                                                if(booking.nPartecipanti == actualbook.nPartecipanti)
                                                    if (actualbook.nOre in booking.nOre..booking.nOre+durata)
                                                        database.getReference("bookings/${firm.id}/$id-${actualbook.yy}${isZero(actualbook.mm.toInt())}${actualbook.mm}${isZero(actualbook.dd.toInt())}${actualbook.dd}-${isZero(actualbook.nOre.toInt())}${actualbook.nOre}").removeValue()
                                }
                            }
                        }
                    }
                }
                ds.isDeleted(mContext)
                referenceBooking.removeEventListener(this)
            }
        })
    }

    private fun isZero(i: Int): String{
        return if(i < 10)
            "0"
        else
            ""
    }

    fun compareBookings(userID: String, oraInizio: Long, durata: Long, ds: DataAlreadyBookin){
        var isAlreadyBooked = false
        readAllFirmFromDB(object: DataStatusHashFirm{
            override fun dataisLoadedFirm(firms: HashMap<String, Firm>) {
                referenceBooking.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            for(firmID in snapshot.children) { // in questo punto id azienda
                                for(userBooks in firmID.children){
                                    if(userBooks.key.toString().substring(0,37) == userID){
                                        val confrBook = Booking()
                                        for(field in userBooks.children){
                                            when(field.key){
                                                "dd" -> confrBook.dd = field.value as Long
                                                "mm" -> confrBook.mm = field.value as Long
                                                "yy" -> confrBook.yy = field.value as Long
                                                "nore" -> confrBook.nOre = field.value as Long
                                                "npartecipanti" -> confrBook.nPartecipanti = field.value as Long
                                            }
                                        }
                                        for(i in 0..durata){
                                            if((firms[firmID.key]!!.startHour+confrBook.nOre == oraInizio+i) || (firms[firmID.key]!!.startHour+confrBook.nOre+1 == oraInizio+i)){
                                                isAlreadyBooked = true
                                                break
                                            }
                                        }
                                    }
                                    if(isAlreadyBooked){
                                        break
                                    }
                                }
                                if(isAlreadyBooked){
                                    break
                                }
                            }
                            ds.alreadyBooked(isAlreadyBooked)
                            referenceBooking.removeEventListener(this)
                        }
                    }
                })
            }
        })
    }

    fun removeUser(){
        val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
        referenceBooking.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (azienda in snapshot.children){
                        for (bookings in azienda.children){
                            if (bookings.key!!.split("-")[0].trim()==id){
                                database.getReference("bookings/${azienda.key}/${bookings.key}").removeValue()
                            }
                        }
                    }
                }
            }
        })
        database.getReference("users/$id").removeValue()
    }
}