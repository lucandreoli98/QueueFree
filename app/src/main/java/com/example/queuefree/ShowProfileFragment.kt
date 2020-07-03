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
import kotlinx.android.synthetic.main.confirm_password.view.cancPasswordButton
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_show_profile.*
import kotlinx.android.synthetic.main.fragment_show_profile.view.*
import kotlinx.android.synthetic.main.update_password.*
import kotlinx.android.synthetic.main.update_password.view.*

class ShowProfileFragment: Fragment() {

    private lateinit var user: User
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val usersDB = FirebaseDatabase.getInstance().getReference("users")
    private val id = currentUser!!.uid.trim { it <= ' ' }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_profile, container, false)

        val fb: FirebaseDatabaseHelper = FirebaseDatabaseHelper()


        fb!!.readUserFromDB(object : FirebaseDatabaseHelper.DataStatus {
            override fun DataIsLoaded(u: User) {
                user = u
                if(currentUser!!.getIdToken(false).result.signInProvider != "password"){
                    view.passCancButton.visibility = View.INVISIBLE
                    view.editProfileButton.visibility=View.INVISIBLE
                }

                // parte visibile inizialmente
                view.nameSurnameText.text = "${user.nome} ${user.cognome}"
                view.emailTextView.text = user.email
                view.dataTextView.text = "${user.dd}/${user.mm}/${user.yy}"
                // parte invisibile
                view.nameEditText.setText(user.nome)
                view.surnameEditText.setText(user.cognome)
                view.emailEditText.setText(user.email)
                view.dataEditTextView.setText(view.dataTextView.text)

                // modifica del profilo generale
                view.editProfileButton.setOnClickListener {
                    if (view.nameSurnameText.visibility == View.VISIBLE) {
                        changeVisibility()
                    } else {
                        updateProfile()
                    }
                }

                // modifica della password dell'utente
                view.passCancButton.setOnClickListener {
                    // Se e' impostato su modifica password
                    if (view.passCancButton.text.equals(resources.getString(R.string.modifica_password))) {
                        editPassword()
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

    fun updateProfile() {
        // estrazionde dei dati dai editText
        val name = nameEditText.text.toString().trim()
        val surname = surnameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        var ok: Boolean = true

        // controllo se sono vuoti gli editText
        // TODO: CONTROLLARE I MESSAGGI DI ERRORE
        if (ok && name.isEmpty()) {
            nameEditText.error = resources.getString(R.string.passEmpty)
            nameEditText.requestFocus()
            ok = false
        }
        if (ok && surname.isEmpty()) {
            surnameEditText.error = resources.getString(R.string.passEmpty)
            surnameEditText.requestFocus()
            ok = false
        }
        if (ok && email.isEmpty()) {
            emailEditText.error = resources.getString(R.string.emailEmpty)
            emailEditText.requestFocus()
            ok = false
        }

        // se non ci sono campi vuoti
        if(ok){
            val passDialogView =
                LayoutInflater.from(context).inflate(R.layout.confirm_password, null)
            val mBuilder = AlertDialog.Builder(context).setView(passDialogView)
            val alertDialog = mBuilder.show()

            passDialogView.okPassButton.setOnClickListener {
                val password = passDialogView.confirmPasswordEditText.text.toString().trim()

                currentUser.let { cUser ->
                    val credential = EmailAuthProvider.getCredential(user.email, password)

                    cUser!!.reauthenticate(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            alertDialog.dismiss()
                            val email = emailEditText.text.toString().trim()
                            val newUser = User(name, surname, email, user.dd, user.mm, user.yy)

                            cUser.updateEmail(email).addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    Toast.makeText(activity, "Update del profilo avvenuto con successo", Toast.LENGTH_LONG).show()
                                    usersDB.child(id).setValue(newUser)
                                    user = newUser
                                    updateLayout(newUser)
                                } else {
                                    Toast.makeText(activity, "ERRORE NELL'UPDATE", Toast.LENGTH_LONG).show()
                                }
                            }


                        } else {
                            passDialogView.confirmPasswordEditText.error = "Error"
                        }
                    }
                }
            }

            passDialogView.cancPasswordButton.setOnClickListener {
                alertDialog.dismiss()
            }

        }
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

    fun editPassword(){
        val newPassDialogView = LayoutInflater.from(context).inflate(R.layout.update_password, null)
        val mBuilder = AlertDialog.Builder(context).setView(newPassDialogView)
        val alertDPasswordDialog = mBuilder.show()

        newPassDialogView.okNewPassButton.setOnClickListener {
            var ok = true
            val email = newPassDialogView.emailForPass.text.toString()
            val oldPassString = newPassDialogView.oldPassword.text.toString()
            val newPasswordString = newPassDialogView.newPassword.text.toString()

            if (ok && email.isEmpty()) {
                newPassDialogView.emailForPass.error = resources.getString(R.string.emailEmpty)
                newPassDialogView.emailForPass.requestFocus()
                ok = false
            }
            if (ok && oldPassString.isEmpty()) {
                newPassDialogView.oldPassword.error = resources.getString(R.string.passEmpty)
                newPassDialogView.oldPassword.requestFocus()
                ok = false
            }
            if (ok && newPasswordString.isEmpty()) {
                newPassDialogView.newPassword.error = resources.getString(R.string.passEmpty)
                newPassDialogView.newPassword.requestFocus()
                ok = false
            }
            if(ok && newPasswordString != newPassDialogView.confirmNewPassword.text.toString()){
                newPassDialogView.confirmNewPassword.error = resources.getString(R.string.confpassDifferent)
                ok = false
            }

            if(ok) {
                currentUser.let { cUser ->
                    val credential = EmailAuthProvider.getCredential(email, oldPassString)

                    cUser!!.reauthenticate(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            cUser.updatePassword(newPasswordString).addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    alertDPasswordDialog.dismiss()
                                    Toast.makeText(
                                        activity,
                                        "Update della password avvenuto con successo",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        activity,
                                        task2.exception.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        } else {
                            newPassDialogView.oldPassword.error = "Password o email errata"
                        }
                    }
                }
            }
        }

        newPassDialogView.cancelButton.setOnClickListener {
            alertDPasswordDialog.dismiss()
        }
    }
}