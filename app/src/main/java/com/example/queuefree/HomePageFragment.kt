package com.example.queuefree

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.homepage_fragment.view.*

class HomePageFragment : Fragment(){// , NavigationView.OnNavigationItemSelectedListener {

    private var fireBase: FirebaseAuth? = null

    /*var toolbar: Toolbar? = null
    var drawerLayout: DrawerLayout? = null
    var navView: NavigationView? = null*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.homepage_fragment, container, false)

        fireBase = FirebaseAuth.getInstance()

        // for the navigation bar
        /*toolbar = view.findViewById(R.id.toolbar)
        (activity as HomePageActivity).setSupportActionBar(toolbar)
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(activity, drawerLayout, toolbar, 0, 0)

        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
        navView?.setNavigationItemSelectedListener(this)*/

        view.spiaggiaButton.setOnClickListener(){
            startActivity(Intent(activity, PrenotazioneSpiagge::class.java))


        }
        view.biblioButton.setOnClickListener(){

        }
        view.museiButton.setOnClickListener(){

        }

        return view
    }

    /*override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id){
            R.id.nav_profile -> startActivity(Intent(activity, ProflileActivity::class.java))
            R.id.nav_logout -> fireBase!!.signOut()
        }

        return true
    }*/
}