package com.example.queuefree

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.facebook.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*  // evita di fare il findViewbyId
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider


class LoginFragment : Fragment() {

    var fireBase: FirebaseAuth? = null
    var flistner: FirebaseAuth.AuthStateListener? = null
    var callbackManager = CallbackManager.Factory.create();
    var TAG="FACEBOOK"

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


        // Initialize Facebook Login button
        view.buttonFacebookLogin.setOnClickListener{
            view.buttonFacebookLogin.setReadPermissions("email", "public_profile")

            view.buttonFacebookLogin.registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {


                    override fun onSuccess(loginResult: LoginResult) {
                        Log.d(TAG, "facebook:onSuccess:$loginResult")
                        Toast.makeText(
                            context!!, "Authentication success1.",
                            Toast.LENGTH_SHORT
                        ).show()
                        handleFacebookAccessToken(loginResult.accessToken)
                    }

                    override fun onCancel() {
                        Log.d(TAG, "facebook:onCancel")
                        // ...
                    }

                    override fun onError(error: FacebookException) {
                        Log.d(TAG, "facebook:onError", error)
                        // ...
                    }
                })
            // ...
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken){
        Log.d(TAG, "Log in successful $token")

        val credential  = FacebookAuthProvider.getCredential(token.token)
        fireBase!!.signInWithCredential(credential).addOnCompleteListener(activity!!) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")
                val user = fireBase!!.currentUser
                Toast.makeText(context!!, "Authentication success.",
                    Toast.LENGTH_SHORT).show()
                startActivity(Intent(activity, HomePageActivity::class.java))
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                Toast.makeText(context!!, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()

            }

        }
    }
}