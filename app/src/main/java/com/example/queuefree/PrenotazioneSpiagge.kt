package com.example.queuefree

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PrenotazioneSpiagge : FragmentActivity(), OnMapReadyCallback{


    lateinit var map: GoogleMap
    lateinit var mapFragment: SupportMapFragment





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_activity)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        val geo = Geocoder(this)



        val database:FirebaseDatabaseHelper= FirebaseDatabaseHelper()



        database.readFirmsandtakeAdress(object : FirebaseDatabaseHelper.DataStatusFirm {
            override fun DataisLoadedFirm(firm: Firm) {


                geo.getFromLocationName(firm.location, 1)

                val latlng = LatLng(
                    geo.getFromLocationName(firm.location, 1).get(0).latitude,
                    geo.getFromLocationName(
                        firm.location, 1
                    ).get(0).longitude
                )

                if (intent.getStringExtra("tipo") == "Spiaggia") {
                    map.addMarker(
                        MarkerOptions().position(latlng).title(firm.nome)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ombrellone))
                    )
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10f))
                } else if (intent.getStringExtra("tipo") == "Museo") {
                    map.addMarker(
                        MarkerOptions().position(latlng).title(firm.nome)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.museo))
                    )
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10f))
                } else if (intent.getStringExtra("tipo") == "Biblioteca") {
                    map.addMarker(
                        MarkerOptions().position(latlng).title(firm.nome)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.libro))
                    )
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10f))
                }


            }

        }, intent.getStringExtra("tipo")!!)






        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(42.76698, 12.493823), 6f))


    }

}