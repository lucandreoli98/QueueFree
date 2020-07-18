package com.example.queuefree

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.*
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
        val removeDialogView = LayoutInflater.from(this).inflate(R.layout.remove_user, null)
        val mBuilder = AlertDialog.Builder(this).setView(removeDialogView)
        val alertDialog = mBuilder.show()

        removeDialogView.okUserPassButton.setOnClickListener {
            alertDialog.dismiss()
            val user = Firebase.auth.currentUser!!

            when(user.getIdToken(false).result.signInProvider){
                "password" -> {
                    val passDialogView = LayoutInflater.from(this).inflate(R.layout.confirm_password,null)
                    val builderPass = AlertDialog.Builder(this).setView(passDialogView)
                    val alertPassDialog = builderPass.show()

                    passDialogView.okPassButton.setOnClickListener {
                        val password = passDialogView.confirmPasswordEditText.text.toString().trim()

                        if(password.isEmpty()){
                            passDialogView.confirmPasswordEditText.error = resources.getString(R.string.passEmpty)
                            passDialogView.confirmPasswordEditText.requestFocus()
                        } else{
                            val credential = EmailAuthProvider.getCredential(user.email!!, password)
                            user.let { cUser ->
                                cUser.reauthenticate(credential).addOnCompleteListener { task ->
                                    if(task.isSuccessful){
                                        FirebaseDatabaseHelper().removeUser(this,object : FirebaseDatabaseHelper.DataStatusCancel{
                                            override fun isDeleted(mContext: Context) {
                                                cUser.delete().addOnCompleteListener {taskDelete ->
                                                    if(taskDelete.isSuccessful){
                                                        Toast.makeText(mContext,"Rimozione del profilo avvenuto con successo",Toast.LENGTH_SHORT).show()
                                                        startActivity(Intent(mContext,MainActivity::class.java))
                                                    }
                                                }
                                            }

                                        })

                                    }else
                                        Toast.makeText(this, "Password errata", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    passDialogView.cancPasswordButton.setOnClickListener {
                        alertPassDialog.dismiss()
                    }
                }
                "google.com" -> {
                    val googleAccount = GoogleSignIn.getLastSignedInAccount(this)

                    if (googleAccount != null){
                        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
                        user.let { cUser ->
                            cUser.reauthenticate(credential).addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    FirebaseDatabaseHelper().removeUser(this, object : FirebaseDatabaseHelper.DataStatusCancel{
                                        override fun isDeleted(mContext: Context) {
                                            cUser.delete().addOnCompleteListener {taskDelete ->
                                                if(taskDelete.isSuccessful){
                                                    Toast.makeText(mContext,"Rimozione del profilo avvenuto con successo",Toast.LENGTH_SHORT).show()
                                                    startActivity(Intent(mContext,MainActivity::class.java))
                                                }
                                            }
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
                "facebook.com" -> {
                    FirebaseDatabaseHelper().removeUser(this, object : FirebaseDatabaseHelper.DataStatusCancel{
                        override fun isDeleted(mContext: Context) {
                            FirebaseAuth.getInstance().signOut()
                            LoginManager.getInstance().logOut()
                            startActivity(Intent(mContext, MainActivity::class.java))
                        }
                    })
                }
            }
        }
    }
}
