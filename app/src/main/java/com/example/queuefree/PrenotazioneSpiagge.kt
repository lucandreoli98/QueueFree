package com.example.queuefree

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PrenotazioneSpiagge : FragmentActivity(), OnMapReadyCallback{


    lateinit var map: GoogleMap
    lateinit var mapFragment: SupportMapFragment
    lateinit var geo:Geocoder
    lateinit var firms:ArrayList<Firm>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_activity)
        firms= ArrayList()
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment









        mapFragment!!.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(42.76698,12.493823), 6f))


        val database:FirebaseDatabaseHelper= FirebaseDatabaseHelper()
         database.readFirmsandtakeAdress(object : FirebaseDatabaseHelper.DataStatusFirm {
            override fun DataisLoadedFirm(fr: Firm){


                geo.getFromLocationName(fr.location,1)
                var latlng =LatLng( geo.getFromLocationName(fr.location,1).get(0).latitude,geo.getFromLocationName(fr.location,1).get(0).longitude)
                map!!.addMarker( MarkerOptions().position(latlng).title(fr.location))
                map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10f))


            }

        },"Spiaggia")!!
    }


}