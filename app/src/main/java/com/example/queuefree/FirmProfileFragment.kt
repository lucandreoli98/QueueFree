package com.example.queuefree

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.ask_days.view.*
import kotlinx.android.synthetic.main.ask_how_take_picture.view.*
import kotlinx.android.synthetic.main.confirm_password.view.*
import kotlinx.android.synthetic.main.fragment_firm_profile.view.*
import kotlinx.android.synthetic.main.fragment_firm_showprofile.*
import kotlinx.android.synthetic.main.fragment_firm_showprofile.view.*
import kotlinx.android.synthetic.main.fragment_firm_showprofile.view.emailTextViewFirm
import kotlinx.android.synthetic.main.fragment_show_profile.*
import kotlinx.android.synthetic.main.time_picker_layout.view.*
import kotlinx.android.synthetic.main.update_password.view.*
import java.io.ByteArrayOutputStream

class FirmProfileFragment: Fragment() {

    private lateinit var firm: Firm
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val firmDB = FirebaseDatabase.getInstance().getReference("firm")
    private val id = currentUser!!.uid.trim { it <= ' ' }
    private var passDialogView:View? = null
    private val fb: FirebaseDatabaseHelper = FirebaseDatabaseHelper()
    private val RIC = 1234
    private val AGC = 5678
    private var minuteopentime=0
    private var houropentime=0
    private var minuteclosetime=0
    private var hourclosetime=0
    private lateinit var imageUri: Uri
    private var vista:View? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_firm_showprofile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vista=view


        fb.readFirmFromDB(object : FirebaseDatabaseHelper.DataStatusFirm {
            override fun DataisLoadedFirm(f: Firm) {

                houropentime = f.startHour.toInt()
                hourclosetime = f.endHour.toInt()
                minuteclosetime = f.endMinute.toInt()
                minuteopentime = f.startMinute.toInt()

                view.firm_name_text.text = f.nomeazienza
                view.emailTextViewFirm.text = f.email
                view.locationTextView.text = f.location
                view.totalPeopleTextView.text = f.capienza.toString()
                view.maxGruppoTextView.text = f.maxPartecipants.toString()
                view.FasciaTextView.text = f.maxTurn.toString()
                view.openNumberText.text = completeTimeStamp(f.startHour, f.startMinute)
                view.closeNumberText.text = completeTimeStamp(f.endHour, f.endMinute)
                view.giorniOpen.text = f.giorni

                view.openNumberEditText.text = completeTimeStamp(f.startHour, f.startMinute)
                view.closeNumberEditText.text = completeTimeStamp(f.endHour, f.endMinute)
                view.totalPeopledEditText.setText(f.capienza.toString())
                view.FasciaEditText.setText(f.maxTurn.toString())
                view.maxGruppoEditText.setText(f.maxPartecipants.toString())


                // modifica del profilo generale
                view.edit_firm.setOnClickListener {
                    if (view.totalPeopleTextView.visibility == View.VISIBLE) {
                        changeVisibility()
                    } else {
                        updateProfile(f)
                    }
                }

                view.giorniOpen.setOnClickListener {
                    val openDaysDialogView =
                        LayoutInflater.from(context).inflate(R.layout.ask_days, null)
                    val mBuilder = AlertDialog.Builder(context).setView(openDaysDialogView)
                    val alertOpenDialog = mBuilder.show()

                    openDaysDialogView.luncheck.isChecked

                }


                // modifica della password dell'utente
                view.pwd_edit.setOnClickListener {
                    // Se e' impostato su modifica password
                    if (view.pwd_edit.text == "Password") {
                        editPassword()
                    } else { // se e' impostato su annulla
                        updateLayout(f)

                    }
                }

                view.open.setOnClickListener {
                    val openHourDialogView =
                        LayoutInflater.from(context).inflate(R.layout.time_picker_layout, null)
                    val mBuilder = AlertDialog.Builder(context).setView(openHourDialogView)
                    val alertOpenDialog = mBuilder.show()

                    openHourDialogView.timePicker.setIs24HourView(true)
                    openHourDialogView.timePicker.setOnTimeChangedListener { _, hour, minute ->

                        houropentime = hour
                        minuteopentime = minute

                    }
                    openHourDialogView.okTimerButton.setOnClickListener {
                        openNumberEditText.text =
                            completeTimeStamp(houropentime.toLong(), minuteopentime.toLong())
                        alertOpenDialog.dismiss()
                    }
                    openHourDialogView.cancTimerButton.setOnClickListener {

                        alertOpenDialog.dismiss()
                    }

                }

                view.close.setOnClickListener {
                    val openHourDialogView =
                        LayoutInflater.from(context).inflate(R.layout.time_picker_layout, null)
                    val mBuilder = AlertDialog.Builder(context).setView(openHourDialogView)
                    val alertOpenDialog = mBuilder.show()

                    openHourDialogView.timePicker.setIs24HourView(true)
                    openHourDialogView.timePicker.setOnTimeChangedListener { _, hour, minute ->

                        hourclosetime = hour
                        minuteclosetime = minute

                    }
                    openHourDialogView.okTimerButton.setOnClickListener {
                        closeNumberEditText.text =
                            completeTimeStamp(hourclosetime.toLong(), minuteclosetime.toLong())
                        alertOpenDialog.dismiss()
                    }
                    openHourDialogView.cancTimerButton.setOnClickListener {
                        alertOpenDialog.dismiss()
                    }
                }
            }
        })

        view.progress_bar_firm.visibility=View.VISIBLE
        FirebaseStorage.getInstance().reference.child("pics").child(id).getBytes(4096*4096).addOnSuccessListener { bytes ->
            val bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            view.imageProfileFirm.setImageBitmap(bitmap)
            view.progress_bar_firm.visibility=View.INVISIBLE
        }

        view.imageProfileFirm.setOnClickListener {
            passDialogView =
                LayoutInflater.from(context).inflate(R.layout.ask_how_take_picture, null)
            val mBuilder = AlertDialog.Builder(context).setView(passDialogView)
            val alertDialog = mBuilder.show()
            passDialogView!!.take.setOnClickListener {
                alertDialog.dismiss()
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
                    pictureIntent.resolveActivity(activity!!.packageManager!!).also {
                        startActivityForResult(pictureIntent, RIC)
                    }

                }
            }
            passDialogView!!.gallery.setOnClickListener {
                alertDialog.dismiss()
                //startActivityForResult(Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI), AGC)
                startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), AGC)
            }
        }
    }

    fun editPassword() {
        val newPassDialogView =
            LayoutInflater.from(context).inflate(R.layout.update_password, null)
        val mBuilder = AlertDialog.Builder(context).setView(newPassDialogView)
        val alertDPasswordDialog = mBuilder.show()

        newPassDialogView.okNewPassButton.setOnClickListener {
            var ok = true
            val email = newPassDialogView.emailForPass.text.toString()
            val oldPassString = newPassDialogView.oldPassword.text.toString()
            val newPasswordString = newPassDialogView.newPassword.text.toString()

            if (ok && email.isEmpty()) {
                newPassDialogView.emailForPass.error =
                    resources.getString(R.string.emailEmpty)
                newPassDialogView.emailForPass.requestFocus()
                ok = false
            }
            if (ok && oldPassString.isEmpty()) {
                newPassDialogView.oldPassword.error =
                    resources.getString(R.string.passEmpty)
                newPassDialogView.oldPassword.requestFocus()
                ok = false
            }
            if (ok && newPasswordString.isEmpty()) {
                newPassDialogView.newPassword.error =
                    resources.getString(R.string.passEmpty)
                newPassDialogView.newPassword.requestFocus()
                ok = false
            }
            if (ok && newPasswordString != newPassDialogView.confirmNewPassword.text.toString()) {
                newPassDialogView.confirmNewPassword.error =
                    resources.getString(R.string.confpassDifferent)
                ok = false
            }

            if (ok) {
                currentUser.let { cUser ->
                    val credential = EmailAuthProvider.getCredential(email, oldPassString)

                    cUser!!.reauthenticate(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            cUser.updatePassword(newPasswordString).addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        alertDPasswordDialog.dismiss()
                                        Toast.makeText(activity, "Update della password avvenuto con successo", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(activity, task2.exception.toString(), Toast.LENGTH_LONG).show()
                                    }
                                }

                        } else {
                            newPassDialogView.oldPassword.error = "Password o email errata"
                        }
                    }
                }
            }
        }

        newPassDialogView.cancelButton.setOnClickListener {
            alertDPasswordDialog.dismiss()
        }
    }

    fun changeVisibility() {

        // editText visibili
        openNumberEditText.visibility = View.VISIBLE
        closeNumberEditText.visibility = View.VISIBLE
        totalPeopledEditText.visibility = View.VISIBLE
        open.visibility= View.VISIBLE
        close.visibility= View.VISIBLE
        FasciaEditText.visibility=View.VISIBLE
        maxGruppoEditText.visibility = View.VISIBLE
        giorniOpen.isClickable = true


        openNumberText.visibility=View.INVISIBLE
        closeNumberText.visibility=View.INVISIBLE
        totalPeopleTextView.visibility=View.INVISIBLE
        FasciaTextView.visibility=View.INVISIBLE
        maxGruppoTextView.visibility = View.INVISIBLE


        pwd_edit.text = "Annulla"
        edit_firm.text = "Salva"

    }

    fun updateLayout(firm: Firm) {

        // textView visibili
        openNumberText.visibility=View.VISIBLE
        closeNumberText.visibility=View.VISIBLE
        totalPeopleTextView.visibility=View.VISIBLE
        FasciaTextView.visibility=View.VISIBLE
        maxGruppoTextView.visibility = View.VISIBLE
        giorniOpen.isClickable = false

        // editText invisibili
        openNumberEditText.visibility=View.INVISIBLE
        closeNumberEditText.visibility=View.INVISIBLE
        totalPeopledEditText.visibility=View.INVISIBLE
        open.visibility=View.INVISIBLE
        close.visibility=View.INVISIBLE
        FasciaEditText.visibility=View.INVISIBLE
        maxGruppoEditText.visibility = View.INVISIBLE

        openNumberText.text = completeTimeStamp(firm.startHour,firm.startMinute)
        totalPeopleTextView.text=firm.capienza.toString()
        closeNumberText.text=completeTimeStamp(firm.endHour,firm.endMinute)
        FasciaTextView.text=firm.maxTurn.toString()
        maxGruppoTextView.text = firm.maxPartecipants.toString()

        // parte invisibile
        edit_firm.text = resources.getString(R.string.editProfileButton)
        pwd_edit.text = resources.getString(R.string.Pwd_firm)
    }

    fun updateProfile(firm:Firm) {
        // estrazionde dei dati dai editText
        val open = openNumberEditText.text.toString().trim()
        val close = closeNumberEditText.text.toString().trim()
        val tot = totalPeopledEditText.text.toString().trim()
        val turno = FasciaEditText.text.toString().trim().toLong()
        val maxGruppo = maxGruppoEditText.text.toString().trim()

        var ok: Boolean = true

        // controllo se sono vuoti gli editText
        // TODO: CONTROLLARE I MESSAGGI DI ERRORE
        if (ok && open.isEmpty()) {
            openNumberEditText.error = resources.getString(R.string.passEmpty)
            nameEditText.requestFocus()
            ok = false
        }
        if (ok && close.isEmpty()) {
            closeNumberEditText.error = resources.getString(R.string.passEmpty)
            closeNumberEditText.requestFocus()
            ok = false
        }
        if (ok && tot.isEmpty()) {
            totalPeopledEditText.error = resources.getString(R.string.emailEmpty)
            totalPeopledEditText.requestFocus()
            ok = false
        }
        if (ok && tot.isEmpty()) {
            FasciaEditText.error ="il campo Numero di turni al giorno è vuoto"
            FasciaEditText.requestFocus()
            ok = false
        }
        if(ok && maxGruppo.isEmpty()){
            maxGruppoEditText.error = "Vuoto" //TODO: MESSAGGIO ERRORE CORRETTO
            maxGruppoEditText.requestFocus()
            ok = false
        }
        else if(turno>hourclosetime-houropentime){
            FasciaEditText.error ="il campo Numero di turni non può essere maggiore dell'orario lavorativo"
            FasciaEditText.requestFocus()
            ok = false
        }

        // se non ci sono campi vuoti
        if(ok){
            val passDialogView =
                LayoutInflater.from(context).inflate(R.layout.confirm_password, null)
            val mBuilder = AlertDialog.Builder(context).setView(passDialogView)
            val alertDialog = mBuilder.show()

            passDialogView.okPassButton.setOnClickListener {
                val password = passDialogView.confirmPasswordEditText.text.toString().trim()

                if (password.isEmpty()) {
                    passDialogView.confirmPasswordEditText.error = resources.getString(R.string.passEmpty)
                    passDialogView.confirmPasswordEditText.requestFocus()
                    ok = false
                }

                if(ok) {
                    currentUser.let { cUser ->
                        val credential = EmailAuthProvider.getCredential(firm.email, password)

                        cUser!!.reauthenticate(credential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                alertDialog.dismiss()
                                val email = emailTextViewFirm.text.toString().trim()

                                this.firm = Firm(currentUser!!.uid,firm.nomeazienza, firm.email, firm.password, firm.categoria,firm.location,firm.startHour,firm.startMinute,firm.endHour,firm.endMinute,tot.toLong(),firm.descrizione,turno,maxGruppo.toLong())

                                cUser.updateEmail(email).addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        Toast.makeText(
                                            activity,
                                            "Update del profilo avvenuto con successo",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        firmDB.child(id).setValue(this.firm)
                                        updateLayout(this.firm)
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "ERRORE NELL'UPDATE",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }


                            } else {
                                passDialogView.confirmPasswordEditText.error =
                                    "Password non corretta"
                            }
                        }
                    }
                }
            }

            passDialogView.cancPasswordButton.setOnClickListener {
                alertDialog.dismiss()
            }

        }
    }

    fun completeTimeStamp(hour :Long,minute: Long):String{
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RIC && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            val baos = ByteArrayOutputStream()
            val storageRef = FirebaseStorage.getInstance()
                .reference
                .child("pics/${FirebaseAuth.getInstance().currentUser!!.uid}")
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val upload = storageRef.putBytes(baos.toByteArray())

            vista!!.progress_bar_firm.visibility = View.VISIBLE

            upload.addOnCompleteListener { uploadTask ->
                if (uploadTask.isSuccessful) {
                    storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                        urlTask.result.let {
                            imageUri = it
                            vista!!.progress_bar_firm.visibility = View.INVISIBLE
                            vista!!.imageProfileFirm.setImageBitmap(imageBitmap)
                        }
                    }
                } else {
                    Log.e("UPLOAD", "ERROR: ${uploadTask.result.error?.message}")
                }
            }
        } else if (requestCode == AGC && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            val image = data.data
            vista!!.progress_bar_firm.visibility=View.VISIBLE
            vista!!.imageProfileFirm.setImageURI(image)
            vista!!.progress_bar_firm.visibility=View.INVISIBLE

            FirebaseStorage.getInstance().reference.child("pics/$id").putFile(image!!)
                .addOnFailureListener {
                    Log.e("UPLOAD FROM GALLERY", it.message)
                }
        }
    }
}