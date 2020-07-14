package com.example.queuefree

import android.content.Context
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.info_prenotazione.view.*
import java.text.SimpleDateFormat
import java.util.*


class InfoTicket: Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    lateinit var map: GoogleMap
    lateinit var mMapView: MapView

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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMapView = view.mappaScreen
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()
        mMapView.getMapAsync(this)


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

    override fun onMapReady(p0: GoogleMap?) {
        MapsInitializer.initialize(context!!)

        map = p0!!
        p0.mapType = GoogleMap.MAP_TYPE_NORMAL


        //val latlan = DownloadFilesTask(activity!!).execute(firm.location).get()

        //val pos = CameraPosition.builder().target(latlan)

        //p0.moveCamera(CameraUpdateFactory.newCameraPosition(pos.build()))

    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return true
    }

    class DownloadFilesTask(private val context: Context) : AsyncTask<String, Void, LatLng>() {
        override fun doInBackground(vararg posizione: String): LatLng? {
            val geo = Geocoder(context)
            return LatLng(geo.getFromLocationName(posizione[0],1)[0].latitude, geo.getFromLocationName(posizione[0],1)[0].longitude)
        }
    }
}