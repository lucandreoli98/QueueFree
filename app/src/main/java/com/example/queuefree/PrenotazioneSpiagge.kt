package com.example.queuefree

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.custom_info_window.view.*

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
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(44.4222, 8.9052), 8f))

        var geo = Geocoder(this)


        val database:FirebaseDatabaseHelper= FirebaseDatabaseHelper()



        database.readFirmsandtakeAdress(object : FirebaseDatabaseHelper.DataStatusFirm {
            override fun DataisLoadedFirm(fr: Firm){


                geo.getFromLocationName(fr.location,1)

                val latlng =LatLng( geo.getFromLocationName(fr.location,1).get(0).latitude,geo.getFromLocationName(
                    fr.location,1).get(0).longitude)

                if(intent.getStringExtra("tipo") == "Spiaggia") {

                    map.addMarker(MarkerOptions().position(latlng).title(fr.nomeazienza).snippet(fr.email).icon(BitmapDescriptorFactory.fromResource(R.drawable.ombrellone)))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,10f))

                }
                else if(intent.getStringExtra("tipo") == "Museo") {
                    map.addMarker(MarkerOptions().position(latlng).title(fr.nomeazienza).snippet(fr.email).icon(BitmapDescriptorFactory.fromResource(R.drawable.museo)))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,10f))

                }
                else if(intent.getStringExtra("tipo") == "Biblioteca") {
                    map.addMarker(MarkerOptions().position(latlng).title(fr.nomeazienza).snippet(fr.email).icon(BitmapDescriptorFactory.fromResource(R.drawable.libro)))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,10f))

                }


            }

        },intent.getStringExtra("tipo"))

        map.setOnMarkerClickListener(this)



    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        val passDialogView = LayoutInflater.from(this).inflate(R.layout.custom_info_window, null)
        if (p0 != null) {
            passDialogView.titlefirm.text = p0.title
            passDialogView.info_firm.text=p0.snippet
        }
        passDialogView.prenota.setOnClickListener {
            val intent = Intent(this,LetsbookActivity::class.java)
            //intent.putExtra("email",p0!!.snippet)
            startActivity(intent)

        }

        val mBuilder = AlertDialog.Builder(this).setView(passDialogView)
        val alertDialog = mBuilder.show()




    return false
    }

}