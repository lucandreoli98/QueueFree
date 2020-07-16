package com.example.queuefree

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class FirmActivity : AppCompatActivity() {

    private val fb = FirebaseDatabaseHelper()
    private val mContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firm_profile)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_firm, FirmFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu,menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout      -> signOut()
            R.id.menu_remove_firm -> removeFirm()
        }

        return true
    }

    fun signOut(){
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        val i=Intent(this, MainActivity::class.java)
        i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }

    // TODO: RIMUOVERE ANCHE DA AUTHENTICATION
    fun removeFirm(){
        fb.readFirmFromDB(object : FirebaseDatabaseHelper.DataStatusFirm{
            override fun DataisLoadedFirm(firm: Firm) {
                fb.removeFirm(mContext,firm,object : FirebaseDatabaseHelper.DataStatusCancel{
                    override fun isDeleted(mContext: Context) {
                        startActivity(Intent(mContext, MainActivity::class.java))
                    }
                })
            }
        })
    }
}