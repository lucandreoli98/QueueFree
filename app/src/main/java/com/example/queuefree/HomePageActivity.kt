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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class HomePageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var fireBase: FirebaseAuth? = null
    val CHANNEL_ID1="notificationChannel1"
    val CHANNEL_ID2="notificationChannel2"
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        notificationchannel1()
        Log.d("CHANNEL1", "Canale creato")
        notificationchannel2()
        Log.d("CHANNEL2", "Canale creato")
        // start service
        val service = Intent(this, ReminderBookings::class.java)
        startService(service)
        Log.d("INTENT1", "Intent fatto")
        val service2 = Intent(this, FinishTurnReminder::class.java)
        startService(service2)
        Log.d("INTENT2", "Intent fatto")


        fireBase = FirebaseAuth.getInstance()

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_home, HomePageFragment()).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.nav_profile -> startActivity(Intent(this, ProflileActivity::class.java))
            R.id.nav_about -> about()
            R.id.nav_logout -> signOut()
        }

        return true
    }

    fun about(){
        val aboutDialogView = LayoutInflater.from(this).inflate(R.layout.activity_about, null)
        val mBuilder = AlertDialog.Builder(this).setView(aboutDialogView)
        mBuilder.show()
    }

    fun signOut(){
        fireBase!!.signOut()
        LoginManager.getInstance().logOut()
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        val i=Intent(this, MainActivity::class.java)
        i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }

    fun notificationchannel1(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.notification_channel_start)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID1, name, importance)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    fun notificationchannel2(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.notification_channel_start)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID2, name, importance)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }


}
