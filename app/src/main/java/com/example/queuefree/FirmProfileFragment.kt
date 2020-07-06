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
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.ask_how_take_picture.view.*
import kotlinx.android.synthetic.main.confirm_password.view.*
import kotlinx.android.synthetic.main.fragment_firm_profile.*
import kotlinx.android.synthetic.main.fragment_firm_profile.view.*
import kotlinx.android.synthetic.main.fragment_firm_profile.view.emailTextViewFirm
import kotlinx.android.synthetic.main.fragment_register_firm.view.*
import kotlinx.android.synthetic.main.fragment_show_profile.*
import kotlinx.android.synthetic.main.fragment_show_profile.view.*
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
    var minuteopentime=0L
    var houropentime=0L
    var minuteclosetime=0L
    var hourclosetime=0L


    private lateinit var imageUri: Uri
    private var vista:View? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_firm_profile, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vista=view


        fb.readFirmFromDB(object : FirebaseDatabaseHelper.DataStatusFirm {
            override fun DataisLoadedFirm(f: Firm) {
                firm = f
                view.nameFirmText.text = firm.nomeazienza
                view.emailTextViewFirm.text = firm.email
                view.locationTextView.text = firm.location
                view.totalPeopleTextView.text = firm.capienza.toString()


                var open=""
                var close=""


                if (firm.startHour < 10 && firm.startMinute < 10)
                    open = "0${firm.startHour}:0${firm.startMinute}"
                else if (firm.startHour < 10 && firm.startMinute > 10)
                    open = "0${firm.startHour}:${firm.startMinute}"
                else if (firm.startHour > 10 && firm.startMinute < 10)
                    open = "${firm.startHour}:0${firm.startMinute}"
                else
                   open = "${firm.startHour}:${firm.startMinute}"

                view.openNumberText.text=open


                if (firm.endHour < 10 && firm.endMinute < 10)
                   close= "0${firm.endHour}:0${firm.endMinute}"
                else if (firm.endHour < 10 && firm.endMinute > 10)
                    close = "0${firm.endHour}:${firm.endMinute}"
                else if (firm.endHour > 10 && firm.endMinute < 10)
                    close = "${firm.endHour}:0${firm.endMinute}"
                else
                   close = "${firm.endHour}:${firm.endMinute}"

                view.closeNumberText.text=close

                view.openNumberEditText.setText(open)
                view.closeNumberEditText.setText(close)
                view.totalPeopledEditText.setText(view.totalPeopleTextView.text)


                // modifica del profilo generale
                view.edit_firm.setOnClickListener {
                    if (view.totalPeopleTextView.visibility == View.VISIBLE) {
                        changeVisibility()
                    } else {
                        updateProfile(f)
                    }
                }



                // modifica della password dell'utente
                view.pwd_edit.setOnClickListener {
                    // Se e' impostato su modifica password
                    if (view.pwd_edit.text =="Password") {
                        editPassword()
                    } else { // se e' impostato su annulla
                        updateLayout(firm)

                    }
                }

                view.open.setOnClickListener {
                    val openHourDialogView = LayoutInflater.from(context).inflate(R.layout.time_picker_layout, null)
                    val mBuilder = AlertDialog.Builder(context).setView(openHourDialogView)
                    val alertOpenDialog = mBuilder.show()

                    openHourDialogView.timePicker.setIs24HourView(true)
                    openHourDialogView.timePicker.setOnTimeChangedListener { timePicker, hour, minute ->

                        houropentime=hour.toLong()
                        minuteopentime=minute.toLong()

                    }
                    openHourDialogView.okTimerButton.setOnClickListener {
                        openNumberEditText.setText(completeTimeStamp(houropentime,minuteopentime))
                        alertOpenDialog.dismiss()
                    }
                    openHourDialogView.cancTimerButton.setOnClickListener {

                        alertOpenDialog.dismiss()
                    }

                }

                view.close.setOnClickListener {
                    val openHourDialogView = LayoutInflater.from(context).inflate(R.layout.time_picker_layout, null)
                    val mBuilder = AlertDialog.Builder(context).setView(openHourDialogView)
                    val alertOpenDialog = mBuilder.show()

                    openHourDialogView.timePicker.setIs24HourView(true)
                    openHourDialogView.timePicker.setOnTimeChangedListener { timePicker, hour, minute ->

                        hourclosetime=hour.toLong()
                        minuteclosetime=minute.toLong()

                    }
                    openHourDialogView.okTimerButton.setOnClickListener {
                        closeNumberEditText.setText(completeTimeStamp(hourclosetime.toLong(),minuteclosetime.toLong())
                        )
                        alertOpenDialog.dismiss()
                    }
                    openHourDialogView.cancTimerButton.setOnClickListener {

                        alertOpenDialog.dismiss()
                    }

                }






            }


        })

        view.progress_bar_firm.visibility=View.VISIBLE
        FirebaseStorage.getInstance().reference.child("pics").child(id).getBytes(1024*1024).addOnSuccessListener { bytes ->
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
                                    cUser.updatePassword(newPasswordString)
                                        .addOnCompleteListener { task2 ->
                                            if (task2.isSuccessful) {
                                                alertDPasswordDialog.dismiss()
                                                Toast.makeText(
                                                    activity,
                                                    "Update della password avvenuto con successo",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    activity,
                                                    task2.exception.toString(),
                                                    Toast.LENGTH_LONG
                                                ).show()
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


        openNumberText.visibility=View.INVISIBLE
        closeNumberText.visibility=View.INVISIBLE
        totalPeopleTextView.visibility=View.INVISIBLE


        pwd_edit.text = "Annulla"
        edit_firm.text = "Salva"

    }

    fun updateLayout(firm: Firm) {


        // textView visibili
        openNumberText.visibility=View.VISIBLE
        closeNumberText.visibility=View.VISIBLE
        totalPeopleTextView.visibility=View.VISIBLE
        // editText invisibili
        openNumberEditText.visibility=View.INVISIBLE
        closeNumberEditText.visibility=View.INVISIBLE
        totalPeopledEditText.visibility=View.INVISIBLE
        open.visibility=View.INVISIBLE
        close.visibility=View.INVISIBLE


        openNumberText.text = completeTimeStamp(firm.startHour,firm.startMinute)
        totalPeopleTextView.text=firm.capienza.toString()
        closeNumberText.text=completeTimeStamp(firm.endHour,firm.endMinute)


        // parte invisibile

        edit_firm.text = resources.getString(R.string.editProfileButton)
        pwd_edit.text = resources.getString(R.string.Pwd_firm)
    }







    fun updateProfile(firm:Firm) {
        // estrazionde dei dati dai editText
        val open = openNumberEditText.text.toString().trim()
        val close = closeNumberEditText.text.toString().trim()
        val tot = totalPeopledEditText.text.toString().trim()
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
                                val newFirm = Firm(firm.nomeazienza, firm.email, firm.password, firm.categoria,firm.location,houropentime,minuteopentime,hourclosetime,minuteclosetime,tot.toLong(),firm.descrizione,firm.maxTurn,firm.maxPartecipants)

                                cUser.updateEmail(email).addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        Toast.makeText(
                                            activity,
                                            "Update del profilo avvenuto con successo",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        firmDB.child(id).setValue(newFirm)
                                        updateLayout(newFirm)
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

            vista!!.progress_bar.visibility = View.VISIBLE
            upload.addOnCompleteListener { uploadTask ->
                if (uploadTask.isSuccessful) {
                    storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                        urlTask.result.let {
                            imageUri = it
                            vista!!.progress_bar.visibility = View.INVISIBLE
                            vista!!.imageProfile.setImageBitmap(imageBitmap)
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








    /*private fun signOut(){
        fireBase!!.signOut()

        LoginManager.getInstance().logOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

        val googleSignInClient = GoogleSignIn.getClient(context!!, gso)
        googleSignInClient.signOut()

        val i= Intent(context!!, MainActivity::class.java)
        i.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }*/
