package com.example.queuefree

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*  // evita di fare il findViewbyId

class LoginFragment : Fragment() {

    var fireBase: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        fireBase = FirebaseAuth.getInstance()

        // Se si e' gia' loggati nell'applicazione si viene reindirizzati alla homePage
        if (fireBase != null && fireBase!!.currentUser != null) {
            startActivity(Intent(activity, HomePageActivity::class.java))
        }

        // Se viene cliccato il testo "REGISTRATI"
        view.signUpTextView.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.login_fragment, RegisterFragment())
                .commit()
        }

        // Se viene cliccato il tasto di login
        view.loginButtonlogin.setOnClickListener {

            if (fireBase!!.currentUser != null) {

                startActivity(Intent(activity, HomePageActivity::class.java))

            } else {

                val email = emailLoginEditText.text.toString().trim()
                val password = passLoginEditText.text.toString().trim()
                var ok=true     //ti evita di fare le operazioni in piu per ogni controllo basta che un campo non sia compilato
                // controllo se i campi email e password sono vuoti
                if (ok && email.isEmpty()) {
                    emailLoginEditText.error = resources.getString(R.string.emailEmpty)
                    emailLoginEditText.requestFocus()
                    ok=false
                }
                if (ok && password.isEmpty()) {
                    passLoginEditText.error = resources.getString(R.string.passEmpty)
                    passLoginEditText.requestFocus()
                    ok=false
                }

                if(ok)
                    // check dell'email e password su database
                    fireBase!!.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // autenticazione è avvenuta con successo e l'utente viene reindirizzato verso la homePage
                                startActivity(Intent(activity, HomePageActivity::class.java))
                            } else {
                                Toast.makeText(activity,"Email o password errate", Toast.LENGTH_LONG).show();
                                Log.e("Login", "signInWithEmail:failure", task.exception)
                            }
                        }
                        .addOnFailureListener{
                            Toast.makeText(activity!!,it.message,Toast.LENGTH_SHORT).show()
                        }
            }
        }
        return view
    }
}