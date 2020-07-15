package com.example.queuefree

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.ask_ifyouaresure.view.*
import kotlinx.android.synthetic.main.biglietto_della_fiumara.view.*
import kotlinx.android.synthetic.main.info_prenotazione.*
import kotlinx.android.synthetic.main.info_prenotazione.view.*
import java.text.SimpleDateFormat
import java.util.*


class InfoTicket: FragmentActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private var firm = Firm()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_prenotazione)

        val bundle = intent.getBundleExtra("parameter")

        val mMapView = supportFragmentManager.findFragmentById(R.id.mappaScreen) as SupportMapFragment

        mMapView.getMapAsync(this)

        val firm: Firm = bundle.getSerializable("firm") as Firm
        this.firm = firm
        val booking: Booking = bundle.getSerializable("book") as Booking
        val durata: Long = bundle.getLong("durata")

        Locale.setDefault(Locale.ITALIAN)
        val data: String = SimpleDateFormat("EEEE dd MMMM yyyy").format(Date(booking.yy.toInt()-1900,booking.mm.toInt()-1, booking.dd.toInt()))

        nameFirm.text = firm.nomeazienza
        dataPrenotazione.text = data
        val orario = completeTimeStamp(firm.startHour+booking.nOre,firm.startMinute) + " - " +completeTimeStamp(durata+firm.startHour+booking.nOre, firm.startMinute)
        orarioPrenotazione.text = orario
        partecipantiPrenotazione.text = "Partecipanti: " + booking.nPartecipanti.toString()
        positionPrenotazione.text = firm.location

        deletebook.setOnClickListener {
            val sureDialogView = LayoutInflater.from(this).inflate(R.layout.ask_ifyouaresure, null)
            val mBuilder = AlertDialog.Builder(this).setView(sureDialogView)
            val alertDialog = mBuilder.show()
            sureDialogView.backbuttonsure.setOnClickListener {
                alertDialog.dismiss()
            }
            sureDialogView.confirmsure.setOnClickListener {
                Log.d("AAAAAAAAAAAAAAAAAAAAAA","AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                FirebaseDatabaseHelper().removeBook(this,firm,booking,durata,object :FirebaseDatabaseHelper.DataStatusCancelBook{
                    override fun dataisDeleted(mContext: Context) {
                        Log.d("BBBBBBBBBBBBBBBBBBBBBBBBBBB","BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB")

                        Toast.makeText(mContext,"Prenotazione cancellata con successo!",Toast.LENGTH_SHORT).show()
                        val i = Intent(mContext, ProflileActivity::class.java)
                        startActivity(i)
                    }
                })
            }

        }
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

        val latlng = LatLng(firm.latitude,firm.longitude)
        when(firm.categoria) {
            "Spiaggia" -> {
                p0!!.addMarker(MarkerOptions().position(latlng).title(firm.nomeazienza).icon(BitmapDescriptorFactory.fromResource(R.drawable.ombrellone)))
                p0.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))
            }

            "Museo" -> {
                p0!!.addMarker(MarkerOptions().position(latlng).title(firm.nomeazienza).icon(BitmapDescriptorFactory.fromResource(R.drawable.museo)))
                p0.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))
            }

            "Biblioteca" -> {
                p0!!.addMarker(MarkerOptions().position(latlng).title(firm.nomeazienza).icon(BitmapDescriptorFactory.fromResource(R.drawable.libro)))
                p0.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))
            }
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return true
    }

}