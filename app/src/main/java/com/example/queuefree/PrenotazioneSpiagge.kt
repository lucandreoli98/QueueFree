package com.example.queuefree

import android.app.AlertDialog
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_activity)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(44.4222, 8.9052), 8f))

        val geo = Geocoder(this)

        FirebaseDatabaseHelper().readFirmsandtakeAdress(object : FirebaseDatabaseHelper.DataStatusFirm {
            override fun DataisLoadedFirm(firm: Firm){

                geo.getFromLocationName(firm.location,1)

                val latlng = LatLng( geo.getFromLocationName(firm.location,1)[0].latitude, geo.getFromLocationName(
                    firm.location,1)[0].longitude)


                when(intent.getStringExtra("tipo")) {
                    "Spiaggia" -> {
                        map.addMarker(MarkerOptions().position(latlng).title(firm.nomeazienza).snippet(firm.email).icon(BitmapDescriptorFactory.fromResource(R.drawable.ombrellone)))
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10f))
                    }

                    "Museo" -> {
                        map.addMarker(MarkerOptions().position(latlng).title(firm.nomeazienza).snippet(firm.email).icon(BitmapDescriptorFactory.fromResource(R.drawable.museo)))
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10f))
                    }

                    "Biblioteca" -> {
                        map.addMarker(MarkerOptions().position(latlng).title(firm.nomeazienza).snippet(firm.email).icon(BitmapDescriptorFactory.fromResource(R.drawable.libro)))
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10f))
                    }
                }
            }
        },intent.getStringExtra("tipo")!!)

        map.setOnMarkerClickListener(this)

    }

    override fun onMarkerClick(p0: Marker?): Boolean {

        val passDialogView = LayoutInflater.from(this).inflate(R.layout.custom_info_window, null)
        val mBuilder = AlertDialog.Builder(this).setView(passDialogView)
        val alertDialog = mBuilder.show()

        if (p0 != null) {
            passDialogView.titlefirm.text = p0.title
            passDialogView.info_firm.text=p0.snippet
        }

        passDialogView.prenota.setOnClickListener {
            val intent = Intent(this,LetsbookActivity::class.java)
            if (p0 != null)
                intent.putExtra("email", p0.snippet)

            alertDialog.dismiss()
            startActivity(intent)

        }

        return false
    }
}