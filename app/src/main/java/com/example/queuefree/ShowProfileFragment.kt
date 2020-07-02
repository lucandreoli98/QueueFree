package com.example.queuefree

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.confirm_password.*
import kotlinx.android.synthetic.main.confirm_password.view.*
import kotlinx.android.synthetic.main.fragment_show_profile.*
import kotlinx.android.synthetic.main.fragment_show_profile.view.*

class ShowProfileFragment: Fragment() {

    private lateinit var user: User
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val usersDB = FirebaseDatabase.getInstance().getReference("users")
    private val id = currentUser!!.uid.trim { it <= ' ' }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_profile, container, false)

        val fb: FirebaseDatabaseHelper = FirebaseDatabaseHelper()

        Log.d("FACEBOOK: ", currentUser!!.providerId)
        /*if(currentUser!!.providerId.equals("facebook.com")){
            passCancButton.visibility = View.INVISIBLE

        }*/

        fb!!.readUserFromDB(object : FirebaseDatabaseHelper.DataStatus {
            override fun DataIsLoaded(u: User) {
                user = u
                // parte visibile inizialmente
                nameSurnameText.text = "${user.nome} ${user.cognome}"
                emailTextView.text = user.email
                dataTextView.text = "${user.dd}/${user.mm}/${user.yy}"
                // parte invisibile
                nameEditText.setText(user.nome)
                surnameEditText.setText(user.cognome)
                emailEditText.setText(user.email)
                dataEditTextView.setText(dataTextView.text)

                // modifica del profilo generale
                view.editProfileButton.setOnClickListener {
                    if (nameSurnameText.visibility == View.VISIBLE) {
                        changeVisibility()
                    } else {
                        saveProfile()
                    }
                }

                // modifica della password dell'utente
                view.passCancButton.setOnClickListener {
                    // Se e' impostato su modifica password
                    if (passCancButton.text.equals(resources.getString(R.string.modifica_password))) {
                        //editPassword(user)
                    } else { // se e' impostato su annulla
                        updateLayout(user)
                    }
                }
            }
        })

        return view
    }

    // modifica del profilo
    fun changeVisibility() {
        // textView invisibili
        nameSurnameText.visibility = View.INVISIBLE
        emailTextView.visibility = View.INVISIBLE
        dataTextView.visibility = View.INVISIBLE
        // editText visibili
        nameEditText.visibility = View.VISIBLE
        surnameEditText.visibility = View.VISIBLE
        emailEditText.visibility = View.VISIBLE
        dataEditTextView.visibility = View.VISIBLE

        editProfileButton.text = resources.getString(R.string.saveProfile)
        passCancButton.text = resources.getString(R.string.canc_modifica)
    }

    // salvataggio del profilo
    fun saveProfile() {
        val nome: String = nameEditText.text.toString().trim()
        val cognome = surnameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val newUser = User(nome, cognome, email, user.dd, user.mm, user.yy)

        if(passwordControl(user.email)){
            // reauthenticate the user for the update
            currentUser.let { cUser ->
                cUser?.updateEmail(email)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activity, "Update del profilo", Toast.LENGTH_LONG).show()
                        usersDB.child(id).setValue(newUser)
                        user = newUser
                        updateLayout(newUser)
                    } else {
                        Toast.makeText(activity, "ERRORE NELL'UPDATE", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }

    fun passwordControl(email: String): Boolean {
        val passDialogView = LayoutInflater.from(context).inflate(R.layout.confirm_password, null)
        val mBuilder = AlertDialog.Builder(context).setView(passDialogView)
        val alertDialog = mBuilder.show()
        var isPass = false

        while(!isPass) {
                passDialogView.okPassButton.setOnClickListener {
                val password = passDialogView.confirmPasswordEditText.text.toString().trim()

                currentUser.let { cUser ->
                    val credential = EmailAuthProvider.getCredential(email, password)

                    cUser!!.reauthenticate(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            alertDialog.dismiss()
                            isPass = true
                        } else {
                            passDialogView.confirmPasswordEditText.error = "Error"
                        }
                    }
                }
            }

            passDialogView.cancPasswordButton.setOnClickListener {
                alertDialog.dismiss()
                isPass = true
            }
        }

        return isPass
    }

    fun updateLayout(user: User) {
        // textView visibili
        nameSurnameText.visibility = View.VISIBLE
        emailTextView.visibility = View.VISIBLE
        dataTextView.visibility = View.VISIBLE
        // editText invisibili
        nameEditText.visibility = View.INVISIBLE
        surnameEditText.visibility = View.INVISIBLE
        emailEditText.visibility = View.INVISIBLE
        dataEditTextView.visibility = View.INVISIBLE

        nameSurnameText.setText("${user.nome} ${user.cognome}")
        emailTextView.setText(user.email)
        dataTextView.setText("${user.dd}/${user.mm}/${user.yy}")
        // parte invisibile
        nameEditText.setText(user.nome)
        surnameEditText.setText(user.cognome)
        emailEditText.setText(user.email)
        dataEditTextView.setText(dataTextView.text)

        editProfileButton.text = resources.getString(R.string.editProfileButton)
        passCancButton.text = resources.getString(R.string.modifica_password)
    }


}
