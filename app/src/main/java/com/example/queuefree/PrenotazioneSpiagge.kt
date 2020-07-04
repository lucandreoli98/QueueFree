package com.example.queuefree

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class PrenotazioneSpiagge : FragmentActivity(), OnMapReadyCallback,GoogleMap.OnMarkerClickListener{


    lateinit var map: GoogleMap
    lateinit var mapFragment: SupportMapFragment






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_activity)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(42.76698, 12.493823), 6f))

        var geo = Geocoder(this)


        val database:FirebaseDatabaseHelper= FirebaseDatabaseHelper()



        database.readFirmsandtakeAdress(object : FirebaseDatabaseHelper.DataStatusFirm {
            override fun DataisLoadedFirm(fr: Firm){


                geo.getFromLocationName(fr.location,1)

                var latlng =LatLng( geo.getFromLocationName(fr.location,1).get(0).latitude,geo.getFromLocationName(
                    fr.location,1).get(0).longitude)

                if(intent.getStringExtra("tipo") == "Spiaggia") {
                    map.addMarker(MarkerOptions().position(latlng).title(fr.nome).icon(BitmapDescriptorFactory.fromResource(R.drawable.ombrellone)))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,10f))

                }
                else if(intent.getStringExtra("tipo") == "Museo") {
                    map.addMarker(MarkerOptions().position(latlng).title(fr.nome).icon(BitmapDescriptorFactory.fromResource(R.drawable.museo)))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,10f))

                }
                else if(intent.getStringExtra("tipo") == "Biblioteca") {
                    map.addMarker(MarkerOptions().position(latlng).title(fr.nome).icon(BitmapDescriptorFactory.fromResource(R.drawable.libro)))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,10f))

                }


            }

        },intent.getStringExtra("tipo"))





    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ciao")
        builder.show()


    return false
    }

}