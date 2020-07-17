package com.example.queuefree

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.confirm_password.view.*
import kotlinx.android.synthetic.main.custom_info_window.view.*
import kotlinx.android.synthetic.main.fragment_show_profile.*
import kotlinx.android.synthetic.main.remove_user.view.*
import kotlinx.android.synthetic.main.update_password.view.*


class HomePageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var fireBase: FirebaseAuth? = null
    private val currentUser = FirebaseAuth.getInstance().currentUser
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    val database=FirebaseDatabaseHelper()
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val service = Intent(this, ReminderBookings::class.java)
        startService(service)
        Log.d("INTENT1", "Intent fatto")
        val service2 = Intent(this, FinishTurnReminder::class.java)
        startService(service2)
        Log.d("INTENT2", "Intent fatto")
        Log.d("USER", "${currentUser!!.email}")



        fireBase = FirebaseAuth.getInstance()

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_home, HomePageFragment())
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.nav_profile -> startActivity(Intent(this, ProflileActivity::class.java))
            R.id.nav_about -> about()
            R.id.nav_logout -> signOut()
            R.id.cancel_account -> RemoveUser()
        }

        return true
    }

    fun about() {
        val aboutDialogView = LayoutInflater.from(this).inflate(R.layout.activity_about, null)
        val mBuilder = AlertDialog.Builder(this).setView(aboutDialogView)
        mBuilder.show()
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

                    currentUser.let { cUser ->
                        val credential = EmailAuthProvider.getCredential(cUser!!.email!!, password)
                        Log.d("USER2", "${cUser.email}")

                        cUser!!.reauthenticate(credential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                database.removeUser()
                                alertDialog2.dismiss()
                                val intent = Intent(this,MainActivity::class.java)
                                startActivity(intent)

                            } else {
                                passDialogView2.confirmPasswordEditText.error =
                                    "Password non corretta"
                            }
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
}
