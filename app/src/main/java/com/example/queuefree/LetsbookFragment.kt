package com.example.queuefree

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_letsbook.*
import kotlinx.android.synthetic.main.fragment_letsbook.view.*
import java.util.*
import kotlin.collections.ArrayList

class LetsbookFragment: Fragment(), DatePickerDialog.OnDateSetListener {

    private var day = 0L
    private var month = 0L
    private var year = 0L
    private var v:View? = null
    private val fb = FirebaseDatabaseHelper()
    private var nPeople = 1
    private var firm = Firm()
    private val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
    private val hoursArray: ArrayList<Long> = ArrayList()
    private var isSearch = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_letsbook, container, false)
        v=view

        FirebaseDatabaseHelper().readFirmsfromEmail(arguments!!.getString("email",""), object : FirebaseDatabaseHelper.DataStatusFirm {
            // lettura dell'azienda da DB
            override fun DataisLoadedFirm(f: Firm){
                firm = f
                // compilazione dei campi relativi all'azienda
                view.firmName.text=firm.nomeazienza
                view.startHou.text = completeTimeStamp(firm.startHour,firm.startMinute)
                view.endHou.text = completeTimeStamp(firm.endHour,firm.endMinute)

                // Data del giorno da prenotare
                view.select_data.setOnClickListener {
                    showDatePickerDialog() // apre il pannello del calendario sulla data di oggi
                }

                // Ore possibili da prenotare
                for (i in firm.startHour until firm.endHour)
                    hoursArray.add(i)

                // numero di partecipanti
                val partArray : ArrayList<String> = ArrayList()
                for(i in 1..firm.maxPartecipants)
                    partArray.add(i.toString())

                val b = ArrayAdapter(context!!,android.R.layout.simple_spinner_item,partArray)
                b.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.npeople.adapter = b
                view.npeople.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        // se non viene selezionato niente..
                    }

                    override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        nPeople = position+1 // posizione parte da 0
                        if(day != 0L && month != 0L && year != 0L) {
                            isSearch = true
                            readBooking()
                        }
                    }
                }

                // tempo di permanenza nella struttura
                val durataArray : ArrayList<String> = ArrayList()
                for(i in 1..firm.maxTurn)
                    durataArray.add(i.toString())

                val c = ArrayAdapter(context!!,android.R.layout.simple_spinner_item,durataArray)
                c.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.durataH.adapter = c
            }
        })

        return view
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

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(activity!!, this,
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )

        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        this.day = dayOfMonth.toLong()
        this.month = (month + 1).toLong()
        this.year = year.toLong()
        val date = dayOfMonth.toString() + " / " + (month + 1) + " / " + year
        v!!.select_data.text = date

        isSearch = true
        readBooking() // lettura DB
    }

    private fun readBooking(){
        fb.readDailyBooking(day,month,year,firm, object: FirebaseDatabaseHelper.DataStatusBooking{
            override fun BookingisLoaded(nHour: ArrayList<Long>, bookings: ArrayList<Long>) {
                if(isSearch && nHour.size.toLong() == firm.maxTurn){ // se raggiunto limite massimo di errore
                    Toast.makeText(context!!, "Raggiunto limite massimo di ore giornaliere per il giorno selezionato",Toast.LENGTH_LONG).show()
                    changeVisibilty(false)
                    isSearch = false
                }
                if(isSearch){
                    // selezione dell'orario visibile
                    changeVisibilty(true)

                    // spinner personalizzato
                    val a = SpinnerAdapter(context!!,hoursArray,bookings,nHour,nPeople,firm.startMinute)
                    a.setDropDownViewResource(R.layout.spinner_item)
                    v!!.startHour.adapter = a

                    v!!.book.setOnClickListener {
                        isSearch = true
                        if((v!!.startHour.selectedItemPosition + firm.startHour + v!!.durataH.selectedItemPosition+1) > firm.endHour){
                            isSearch = false
                            Toast.makeText(context!!,"Prenotazione non valida!\nOrario di chiusura non rispettato",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            for (i in v!!.startHour.selectedItemPosition..v!!.durataH.selectedItemPosition + v!!.startHour.selectedItemPosition) {
                                if(nHour.contains(i.toLong())){
                                    isSearch = false
                                    Toast.makeText(context!!,"Le ore selezionate le hai già prenotate precedentemente\nControlla le tue prenotazioni sul profilo",Toast.LENGTH_LONG).show()
                                    break
                                }
                            }

                            if(nHour.size+durataH.selectedItemPosition+1 > firm.maxTurn){
                                isSearch = false
                                Toast.makeText(context!!, "Sforato limite massimo di ore consentite\nLimite ore: ${firm.maxTurn}",Toast.LENGTH_LONG).show()
                            }
                            for (i in v!!.startHour.selectedItemPosition..v!!.durataH.selectedItemPosition + v!!.startHour.selectedItemPosition) {
                                if(bookings[i]<nPeople){
                                    Toast.makeText(context!!,"Prenotazione non valida!\n Posti non sufficienti per l'orario selezionato",Toast.LENGTH_SHORT).show()
                                    isSearch = false
                                    break
                                }
                            }

                            if(isSearch){
                                isSearch = false
                                for (i in v!!.startHour.selectedItemPosition..v!!.durataH.selectedItemPosition + v!!.startHour.selectedItemPosition) {
                                    val r = Booking(day, month, year, i.toLong(), (v!!.npeople.selectedItemPosition+1).toLong())
                                    FirebaseDatabase.getInstance().getReference("/bookings/${firm.id}/$id-${isZero(i)}$i-$year${isZero(month.toInt())}$month${isZero(day.toInt())}$day").setValue(r)
                                }
                                Toast.makeText(context!!,"Prenotazione effettuata con successo!",Toast.LENGTH_SHORT).show()
                                val i = Intent(activity, HomePageActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(i)
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

    private fun changeVisibilty(visible: Boolean){
        if(visible){
            v!!.oraInizio.visibility = View.VISIBLE
            v!!.imageHour.visibility = View.VISIBLE
            v!!.startHour.visibility = View.VISIBLE

            v!!.imageDuration.visibility = View.VISIBLE
            v!!.durata.visibility = View.VISIBLE
            v!!.durataH.visibility = View.VISIBLE

            v!!.book.visibility = View.VISIBLE
        }else{
            v!!.oraInizio.visibility = View.INVISIBLE
            v!!.imageHour.visibility = View.INVISIBLE
            v!!.startHour.visibility = View.INVISIBLE

            v!!.imageDuration.visibility = View.INVISIBLE
            v!!.durata.visibility = View.INVISIBLE
            v!!.durataH.visibility = View.INVISIBLE

            v!!.book.visibility = View.INVISIBLE
        }
    }
}