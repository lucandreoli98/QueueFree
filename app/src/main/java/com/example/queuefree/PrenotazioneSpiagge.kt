package com.example.queuefree




import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


class PrenotazioneSpiagge : FragmentActivity(), OnMapReadyCallback,PlaceSelectionListener {


    private var mMap: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_activity)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?


        autocompleteFragment!!.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME))

        autocompleteFragment!!.setOnPlaceSelectedListener(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    override fun onPlaceSelected(p0: Place) {
        TODO("Not yet implemented")
    }

    override fun onError(p0: Status) {
        TODO("Not yet implemented")
    }
}