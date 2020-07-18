package com.example.queuefree

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.confirm_password.view.*
import kotlinx.android.synthetic.main.remove_user.view.*


class ProflileActivity : AppCompatActivity() {

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
            val user = Firebase.auth.currentUser!!

            when(user.getIdToken(false).result.signInProvider){
                "password" -> {
                    val passDialogView2 = LayoutInflater.from(this).inflate(R.layout.confirm_password,null)
                    val mBuilder2 = AlertDialog.Builder(this).setView(passDialogView2)
                    val alertDialog2 = mBuilder2.show()

                    passDialogView2.okPassButton.setOnClickListener {
                        var ok = true
                        val password =
                            passDialogView2.confirmPasswordEditText.text.toString().trim()
                        if (password.isEmpty()) {
                            passDialogView2.confirmPasswordEditText.error =
                                resources.getString(R.string.passEmpty)
                            passDialogView2.confirmPasswordEditText.requestFocus()
                            ok = false
                        }
                        if (ok) {

                            val credential = EmailAuthProvider.getCredential(user.email!!, password)

                            user.reauthenticate(credential)
                                .addOnSuccessListener {
                                    Log.d("Reautenticate", "User re-authenticated.")
                                    FirebaseDatabaseHelper().removeUser(object: FirebaseDatabaseHelper.DeleteUser {
                                        override fun delUser() {
                                            Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAAAA","AAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                                        }

                                    })
                                    user.delete()
                                        .addOnSuccessListener {
                                            alertDialog2.dismiss()
                                            Log.d("User Eliminato", "User account deleted.")
                                            val i = Intent(this, MainActivity::class.java)
                                            i.flags =
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(i)
                                        }
                                }
                        }
                    }
                    passDialogView2.cancPasswordButton.setOnClickListener {
                        alertDialog2.dismiss()
                    }
                }
                "google.com" -> {
                    val acct = GoogleSignIn.getLastSignedInAccount(this)

                    if (acct != null) {
                        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
                        user.reauthenticate(credential)
                            .addOnSuccessListener{
                                Log.d("Google", "Reauthenticated")
                                FirebaseDatabaseHelper().removeUser(object: FirebaseDatabaseHelper.DeleteUser {
                                    override fun delUser() {
                                        Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAAAA","AAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                                    }

                                })
                                user.delete()
                                    .addOnSuccessListener {
                                        Log.d("User Eliminato", "User account deleted.")
                                        val i = Intent(this, MainActivity::class.java)
                                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(i)
                                    }
                                    .addOnFailureListener {
                                        Log.d("AAAAAAAAAAAAAaa",it.message)
                                    }
                            }
                            .addOnFailureListener {
                                Log.d("Fail", it.message)

                            }
                    }
                }
                "facebook.com" ->{
                    FirebaseDatabaseHelper().removeUser(object: FirebaseDatabaseHelper.DeleteUser {
                        override fun delUser() {
                            Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAAAA","AAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                        }

                    })
                    FirebaseAuth.getInstance().signOut()
                    LoginManager.getInstance().logOut()
                    val i = Intent(this, MainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                }

            }

        }

        passDialogView.cancUserPasswordButton.setOnClickListener {
            alertDialog.dismiss()

        }
    }
}
