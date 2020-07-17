package com.example.queuefree

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.view.* // evita di fare il findViewbyId
import java.util.*

class RegisterFragment: Fragment(), OnDateSetListener {

    private var day = 0L
    private var month = 0L
    private var year = 0L
    private var calendar=Calendar.getInstance()

    // Database
    private var fireBase: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val data=Calendar.getInstance()
        val limite=Calendar.getInstance()
        limite.set(2008,1,1)
        val view = inflater.inflate(R.layout.fragment_register, container, false) // in kotlin tutte le id vengono racchiuse nella view

        fireBase = FirebaseAuth.getInstance()

        view.register_show_calendar.setOnClickListener {
            showDatePickerDialog() // apre il pannello del calendario sulla data di oggi
        }

        view.submitButton.setOnClickListener {
            calendar.set(year.toInt(),month.toInt(),day.toInt())
            val nome = nameRegisterEditText.text.toString().trim()
            val cognome = surnameRegisterEditText.text.toString().trim()
            val email = emailRegisterEditText.text.toString().trim()
            val password = passRegisterEditText.text.toString().trim()
            val conf = confpassRegisterEditText.text.toString().trim()
            var ok=true          //ti evita di fare le operazioni in piu per ogni controllo basta che un campo non sia compilato
            // verifica se tutti i campi sono stati compilati, altrimenti segnala un errore
            if (ok && nome.isEmpty()) {
                nameRegisterEditText.error = resources.getString(R.string.nameEmpty)
                nameRegisterEditText.requestFocus()
                ok=false
            }
            if (ok && cognome.isEmpty()) {
                surnameRegisterEditText.error = resources.getString(R.string.surnameEmpty)
                surnameRegisterEditText.requestFocus()
                ok=false
            }
            if (ok && email.isEmpty()) {
                emailRegisterEditText.error = resources.getString(R.string.emailEmpty)
                emailRegisterEditText.requestFocus()
                ok=false
            }
            if (ok && password.isEmpty()) {
                passRegisterEditText.error = resources.getString(R.string.passEmpty)
                passRegisterEditText.requestFocus()
                ok=false
            }
            if (ok && conf.isEmpty()) {
                confpassRegisterEditText.error = resources.getString(R.string.confpassEmpty)
                confpassRegisterEditText.requestFocus()
                ok=false
            }

            if (ok && password != conf) {
                confpassRegisterEditText.error = resources.getString(R.string.confpassDifferent)
                confpassRegisterEditText.requestFocus()
                ok=false
            }

            if (ok && (day == 0L || month == 0L || year == 0L)) {
                register_show_calendar.text = resources.getString(R.string.dataEmpty)
                register_show_calendar.requestFocus()
                ok=false
            }
            if (ok && (calendar.after(data))){
                register_show_calendar.text = "Errore!Hai inserito una data nel futuro"
                register_show_calendar.requestFocus()
                ok=false
            }
            if (ok && (calendar.after(limite))){
                register_show_calendar.text = "Errore!Hai meno di 12 anni"
                register_show_calendar.requestFocus()
                ok=false
            }

            if(ok)
                fireBase!!.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val u = User(nome, cognome, email, day, month, year)
                            
                            Log.e("task successful", resources.getString(R.string.userRegistrated))
                            
                            val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
                            FirebaseDatabase.getInstance().getReference("/users/$id").setValue(u)
                            FirebaseAuth.getInstance().currentUser!!.sendEmailVerification()
                            fireBase!!.signOut()
                            
                            fragmentManager!!.beginTransaction().replace(R.id.login_fragment, LoginFragment()).commit()
                        } else { 
                            Log.e("task not successful: ", task.exception.toString())
                        }
                    }

                    .addOnFailureListener{
                        Toast.makeText(activity!!,it.message,Toast.LENGTH_SHORT).show()
                    }
            }

        return view
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
        this.month = (month).toLong()
        this.year = year.toLong()
        val date = dayOfMonth.toString() + " / " + (month + 1) + " / " + year
        register_show_calendar.text = date
    }
}