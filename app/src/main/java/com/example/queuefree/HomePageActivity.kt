package com.example.queuefree

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.sign


class HomePageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var fireBase: FirebaseAuth? = null

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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
            R.id.nav_logout -> signOut()
        }

        return true
    }

    fun signOut(){
        fireBase!!.signOut()
        LoginManager.getInstance().logOut();
        val i=Intent(this, MainActivity::class.java)
        i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }
}
