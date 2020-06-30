package com.example.queuefree

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.SearchView
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class PrenotazioneSpiagge : FragmentActivity(), OnMapReadyCallback {


    lateinit var map: GoogleMap
    lateinit var mapFragment: SupportMapFragment
    lateinit var searchView: SearchView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        searchView = findViewById(R.id.sv_location)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val location = searchView!!.getQuery().toString()
                var addressList: List<Address>? = null
                if (location != null || location != " ") {
                    val geocoder = Geocoder(this@PrenotazioneSpiagge)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val address = addressList!![0]
                    val latlng = LatLng(address.latitude, address.longitude)
                    map!!.addMarker(MarkerOptions().position(latlng).title(location))
                    map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10f))
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }
}