package com.example.queuefree

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.info_prenotazione.view.*
import java.text.SimpleDateFormat
import java.util.*

class InfoTicket: Fragment(){//, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var firm = Firm()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.info_prenotazione, container, false)

        val firm: Firm = arguments!!.getSerializable("firm") as Firm
        val booking: Booking = arguments!!.getSerializable("book") as Booking
        val durata: Long = arguments!!.getLong("durata")

        Locale.setDefault(Locale.ITALIAN)
        val data: String = SimpleDateFormat("EEEE dd MMMM yyyy").format(Date(booking.yy.toInt()-1900,booking.mm.toInt()-1, booking.dd.toInt()))

        view.nameFirm.text = firm.nomeazienza
        view.dataPrenotazione.text = data
        val orario = completeTimeStamp(firm.startHour+booking.nOre,firm.startMinute) + " - " +completeTimeStamp(durata+firm.startHour, firm.startMinute)
        view.orarioPrenotazione.text = orario
        view.partecipantiPrenotazione.text = "Partecipanti: " + booking.nPartecipanti.toString()
        view.positionPrenotazione.text = firm.location

        /*val mapFragment = fragmentManager!!.findFragmentById(R.id.mapPrenotazione) as SupportMapFragment
        mapFragment.getMapAsync(this)*/

        return view
    }

    private fun completeTimeStamp(hour :Long, minute: Long):String{
        return if(hour<10){
            if(minute<10){
                "0${hour}:0${minute}"
            } else {
                "0${hour}:${minute}"
            }
        } else if(minute<10){
            "${hour}:0${minute}"
        } else {
            "${hour}:${minute}"
        }
    }

    /*override fun onMapReady(googleMap: GoogleMap?) {
        val geo = Geocoder(context!!)

        var map = googleMap
        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(geo.getFromLocationName(firm.location,1)[0].latitude, geo.getFromLocationName(
            firm.location,1)[0].longitude), 8f))

        map.uiSettings.setAllGesturesEnabled(false)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        TODO("Not yet implemented")
    }*/
}