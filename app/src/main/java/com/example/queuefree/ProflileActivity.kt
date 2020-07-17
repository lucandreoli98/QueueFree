package com.example.queuefree

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.confirm_password.view.*
import kotlinx.android.synthetic.main.remove_user.view.*

class ProflileActivity : AppCompatActivity() {

    private var fireBase: FirebaseAuth? = null
    private val currentUser = FirebaseAuth.getInstance().currentUser
    val database=FirebaseDatabaseHelper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportFragmentManager.beginTransaction().replace(R.id.profileContainer, ProfileFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile_user,menu)

        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_remove_user -> RemoveUser()
        }

        return true
    }



    fun RemoveUser() {
        val passDialogView = LayoutInflater.from(this).inflate(R.layout.remove_user, null)
        val mBuilder = AlertDialog.Builder(this).setView(passDialogView)
        val alertDialog = mBuilder.show()
        passDialogView.okUserPassButton.setOnClickListener {
            alertDialog.dismiss()
            val passDialogView2 = LayoutInflater.from(this).inflate(R.layout.confirm_password,null)
            val mBuilder2 = AlertDialog.Builder(this).setView(passDialogView2)
            val alertDialog2 = mBuilder2.show()

            passDialogView2.okPassButton.setOnClickListener {
                var ok = true
                val password = passDialogView2.confirmPasswordEditText.text.toString().trim()
                if (password.isEmpty()) {
                    passDialogView2.confirmPasswordEditText.error =
                        resources.getString(R.string.passEmpty)
                    passDialogView2.confirmPasswordEditText.requestFocus()
                    ok = false
                }
                if (ok){
                    val user = Firebase.auth.currentUser!!
                    val credential = EmailAuthProvider.getCredential(user.email!!, password)


                    // Get auth credentials from the user for re-authentication. The example below shows
                    // email and password credentials but there are multiple possible providers,
                    // such as GoogleAuthProvider or FacebookAuthProvider.
                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                        .addOnSuccessListener {
                            Log.d("Reautenticate", "User re-authenticated.")
                            database.removeUser()
                            user.delete()
                                .addOnSuccessListener {
                                    alertDialog2.dismiss()
                                    Log.d("User Eliminato", "User account deleted.")
                                    val i = Intent(this, MainActivity::class.java)
                                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(i)
                                }
                        }
                }
            }
            passDialogView2.cancPasswordButton.setOnClickListener {
                alertDialog2.dismiss()
            }

        }

        passDialogView.cancUserPasswordButton.setOnClickListener {
            alertDialog.dismiss()

        }
    }

    fun signOut() {
        fireBase!!.signOut()
        LoginManager.getInstance().logOut()
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }
}
