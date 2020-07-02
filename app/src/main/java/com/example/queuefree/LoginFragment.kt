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
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import org.json.JSONObject


class LoginFragment : Fragment() {

    var fireBase: FirebaseAuth? = null
    var callbackManager = CallbackManager.Factory.create();
    var TAG="FACEBOOK/GOOGLE"
    private var RC_SIGN_IN = 123

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        fireBase = FirebaseAuth.getInstance()
        val gaccount = GoogleSignIn.getLastSignedInAccount(context!!)

        // Se si e' gia' loggati nell'applicazione si viene reindirizzati alla homePage
        if ((fireBase != null && fireBase!!.currentUser != null) || gaccount!=null ) {
            val i=Intent(activity, HomePageActivity::class.java)
            i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)        }

        // Se viene cliccato il testo "REGISTRATI"
        view.signUpTextView.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.login_fragment, RegisterFragment())
                .commit()
        }

        // Se viene cliccato il tasto di login
        view.loginButtonlogin.setOnClickListener {

            if (fireBase!!.currentUser != null) {

                val i=Intent(activity, HomePageActivity::class.java)
                i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
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
                                val i=Intent(activity, HomePageActivity::class.java)
                                i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(i)
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

        view.buttonFacebookLogin.fragment = this
        // Initialize Facebook Login button

        view.buttonFacebookLogin.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(TAG, "facebook:onSuccess:$loginResult")
                    //Toast.makeText( context!!, "Authentication success1.", Toast.LENGTH_SHORT ).show()
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

        //google+

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("506735574426-rq42bk8t1qk9p7pncmv2nhb143734eth.apps.googleusercontent.com")
                .requestEmail()
                .requestId()
                .requestProfile()
                .build()
        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(activity!!, gso)

        view.googleButton.setOnClickListener(){
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }


        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.

            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)

            handleSignInResult(task)
        }else {
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken){
        Log.d(TAG, "Log in successful $token")

        val credential  = FacebookAuthProvider.getCredential(token.token)
        fireBase!!.signInWithCredential(credential)
            .addOnSuccessListener{
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")

                //Toast.makeText(context!!, "Authentication success.", Toast.LENGTH_SHORT).show()
                val profile= Profile.getCurrentProfile()
                if(profile!=null){
                    val request = GraphRequest.newMeRequest(token) { obj: JSONObject, response: GraphResponse ->

                        val day = obj.getString("birthday").substring(3,5).toLong()

                        val month = obj.getString("birthday").substring(0,2).toLong()

                        val year = obj.getString("birthday").substring(6,10).toLong()

                        Log.e("feisbuk",obj.toString())


                        val u = User(obj.getString("first_name"), obj.getString("last_name"), obj.getString("email"), day, month, year)

                        Log.e("task successful", resources.getString(R.string.userRegistrated))

                        val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
                        FirebaseDatabase.getInstance().getReference("/users/$id").setValue(u)
                        FirebaseAuth.getInstance().currentUser!!.sendEmailVerification()

                    }

                    val parameters = Bundle()
                    parameters.putString("fields", "first_name,last_name,gender,birthday,email,id,picture.type(large),link")
                    request.parameters = parameters
                    request.executeAsync()

                    val i=Intent(activity, HomePageActivity::class.java)
                    i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                }
            }
            .addOnFailureListener{
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure ${it.message}")
                Toast.makeText(context!!, "Authentication failed.", Toast.LENGTH_SHORT).show()

            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)!!

            //Toast.makeText(context!!, "Google Authentication success.", Toast.LENGTH_SHORT).show()
            val credential = GoogleAuthProvider.getCredential(account.idToken,null)
            fireBase!!.signInWithCredential(credential)
                .addOnCompleteListener(activity!!) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        // Signed in successfully, show authenticated UI.

                        FirebaseDatabase.getInstance().getReference("/users").orderByChild("email").equalTo(account.email!!).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(!snapshot.exists()){
                                    Log.e("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZz")
                                    val u = User(account.givenName!!, account.familyName!!, account.email!!, 0, 0, 0)
                                    val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
                                    FirebaseDatabase.getInstance().getReference("/users/$id").setValue(u)
                                    FirebaseAuth.getInstance().currentUser!!.sendEmailVerification()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                        Log.e("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ","ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZz")

                        val i=Intent(activity, HomePageActivity::class.java)
                        i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(i)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        // ...
                    }

                    // ...
                }


        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)

        }
    }
}
