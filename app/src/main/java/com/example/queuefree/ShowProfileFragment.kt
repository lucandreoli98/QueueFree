package com.example.queuefree

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_show_profile.*
import kotlinx.android.synthetic.main.fragment_show_profile.view.*

class ShowProfileFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_show_profile, container, false)

        val fb : FirebaseDatabaseHelper = FirebaseDatabaseHelper()

        fb!!.readUserFromDB(object : FirebaseDatabaseHelper.DataStatus {
            override fun DataIsLoaded(u: User) {
                var user = u

                // parte visibile inizialmente
                nameSurnameText.setText("${user.nome} ${user.cognome}")
                emailTextView.setText(user.email)
                dataTextView.setText("${user.dd}/${user.mm}/${user.yy}")
                // parte invisibile
                nameEditText.setText(user.nome)
                surnameEditText.setText(user.cognome)
                emailEditText.setText(user.email)
                dataEditTextView.setText(dataTextView.text)

                // modifica del profilo generale
                view.editProfileButton.setOnClickListener {
                    if(nameSurnameText.visibility == View.VISIBLE){
                        editProfile(user);
                    }
                    else{
                        user = saveProfile(user);
                    }
                }

                // modifica della password dell'utente
                view.passCancButton.setOnClickListener {
                    if(passCancButton.text.equals(resources.getString(R.string.modifica_password))){
                        editPassword(user)
                    }
                    else{
                        updateProfile(user)
                    }
                }
            }
        })

        return view
    }

    fun editProfile(user: User){
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

    fun saveProfile(user: User): User{
        val nome: String = nameEditText.text.toString().trim()
        val cognome = surnameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val newUser = User(nome,cognome,email,user.dd,user.mm,user.yy)

        val usersDB = FirebaseDatabase.getInstance().getReference("users")
        val userAuth = FirebaseAuth.getInstance().currentUser
        val id = userAuth!!.uid.trim { it <= ' ' }

        userAuth.updateEmail(email).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(activity, "Update avvenuto con successo", Toast.LENGTH_LONG).show()
            } else{
                newUser.email = user.email // non aggiorna la password perche' c'e stato un errore
                Toast.makeText(activity, "Errore: ${newUser.email} e ${user.email}", Toast.LENGTH_LONG).show()
            }
        }

        usersDB.child(id).setValue(newUser)

        updateProfile(newUser)
        return newUser
    }

    fun updateProfile(user: User){
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

    fun editPassword(user: User){
        val userDB = FirebaseAuth.getInstance().currentUser
        //userDB.updatePassword()
    }
}
