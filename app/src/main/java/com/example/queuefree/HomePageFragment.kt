package com.example.queuefree

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.askbirthday.view.*
import kotlinx.android.synthetic.main.fragment_register.view.register_show_calendar
import kotlinx.android.synthetic.main.homepage_fragment.view.*
import java.util.*

class HomePageFragment : Fragment(), DatePickerDialog.OnDateSetListener {// , NavigationView.OnNavigationItemSelectedListener {

    private var fireBase: FirebaseAuth? = null
    private var day = 0L
    private var month = 0L
    private var year = 0L
    private var passDialogView:View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.homepage_fragment, container, false)

        fireBase = FirebaseAuth.getInstance()

        FirebaseDatabaseHelper().readUserFromDB(object : FirebaseDatabaseHelper.DataStatus {
            override fun DataIsLoaded(user: User) {
                if ((fireBase != null && fireBase!!.currentUser != null)&&(user.dd==0L)) {
                    passDialogView = LayoutInflater.from(context).inflate(R.layout.askbirthday, null)
                    val mBuilder = AlertDialog.Builder(context).setView(passDialogView)
                    val alertDialog = mBuilder.show()
                    alertDialog.setCancelable(false)

                    passDialogView!!.register_show_calendar.setOnClickListener {
                        showDatePickerDialog() // apre il pannello del calendario sulla data di oggi
                    }

                    passDialogView!!.confdata.setOnClickListener{
                        if (day == 0L || month == 0L || year == 0L) {
                            passDialogView!!.register_show_calendar.text = resources.getString(R.string.insertdate)
                            passDialogView!!.register_show_calendar.requestFocus()
                        }else{
                            val id =
                                FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
                            FirebaseDatabase.getInstance().getReference("/users/$id/dd")
                                .setValue(day)
                            FirebaseDatabase.getInstance().getReference("/users/$id/mm")
                                .setValue(month)
                            FirebaseDatabase.getInstance().getReference("/users/$id/yy")
                                .setValue(year)
                            alertDialog.dismiss()


                        }
                    }


                }
            }
        })

        view.spiaggiaButton.setOnClickListener(){
            val intent = Intent(activity,PrenotazioneSpiagge::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("tipo","Spiaggia")
            startActivity(intent)
        }
        view.biblioButton.setOnClickListener(){
            val intent = Intent(activity,PrenotazioneSpiagge::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("tipo","Biblioteca")
            startActivity(intent)

        }
        view.museiButton.setOnClickListener(){
            val intent = Intent(activity,PrenotazioneSpiagge::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("tipo","Museo")
            startActivity(intent)

        }

        return view
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(activity!!, this,
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )

        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        this.day = dayOfMonth.toLong()
        this.month = (month + 1).toLong()
        this.year = year.toLong()
        val date = dayOfMonth.toString() + " / " + (month + 1) + " / " + year
        passDialogView!!.register_show_calendar.text = date
    }
}