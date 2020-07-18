package com.example.queuefree

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.facebook.Profile
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.ask_how_take_picture.view.*
import kotlinx.android.synthetic.main.confirm_password.view.*
import kotlinx.android.synthetic.main.fragment_show_profile.*
import kotlinx.android.synthetic.main.fragment_show_profile.view.*
import kotlinx.android.synthetic.main.update_password.view.*
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*


class ShowProfileFragment: Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var user: User
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val usersDB = FirebaseDatabase.getInstance().getReference("users")
    private val id = currentUser!!.uid.trim { it <= ' ' }
    private var passDialogView:View? = null
    private val fb: FirebaseDatabaseHelper = FirebaseDatabaseHelper()
    private val RIC = 1234
    private val AGC = 5678
    private var day = 0L
    private var month = 0L
    private var year = 0L
    private var profilePic:Bitmap? = null

    private lateinit var imageUri: Uri
    private lateinit var vista:View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_show_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vista=view
        fb.readUserFromDB(object : FirebaseDatabaseHelper.DataStatus {
            override fun DataIsLoaded(u: User) {
                user = u
                if(currentUser!!.getIdToken(false).result.signInProvider != "password"){
                    view.passCancButton.visibility = View.INVISIBLE
                    view.editProfileButton.visibility = View.INVISIBLE
                }

                // parte visibile inizialmente
                view.nameSurnameText.text = "${user.nome} ${user.cognome}"
                view.emailTextView.text = user.email
                view.dataTextView.text = "${user.dd}/${user.mm}/${user.yy}"
                // parte invisibile
                view.nameEditText.setText(user.nome)
                view.surnameEditText.setText(user.cognome)
                view.emailEditText.setText(user.email)
                view.dataEditTextView.setText(view.dataTextView.text)

                // modifica del profilo generale
                view.editProfileButton.setOnClickListener {
                    if (view.nameSurnameText.visibility == View.VISIBLE) {
                        changeVisibility()
                    } else {
                        updateProfile()
                    }
                }

                // modifica della password dell'utente
                view.passCancButton.setOnClickListener {
                    // Se e' impostato su modifica password
                    if (view.passCancButton.text == resources.getString(R.string.modifica_password)) {
                        editPassword()
                    } else { // se e' impostato su annulla
                        updateLayout(user)
                    }
                }

                view.dataEditTextView.setOnClickListener {
                    showDatePickerDialog() // apre il pannello del calendario sulla data di oggi
                }

            }
        })
        view.progress_bar.visibility=View.VISIBLE
        FirebaseStorage.getInstance().reference.child("pics").child(id).getBytes(4096*4096)
            .addOnSuccessListener { bytes ->
            val bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            view.imageProfile.setImageBitmap(bitmap)
            view.progress_bar.visibility=View.INVISIBLE
            }
            .addOnFailureListener {
                val profile = Profile.getCurrentProfile()
                if (profile != null){
                    val urimage = profile.getProfilePictureUri(4096,4096)
                    profilePic = downloadImage().execute(urimage.toString()).get()

                    view.imageProfile.setImageBitmap(profilePic)
                    view.progress_bar.visibility=View.INVISIBLE
                }else

                FirebaseStorage.getInstance().reference.child("pics").child("defaultimage.jpg").getBytes(4096*4096)
                    .addOnSuccessListener { bytes ->
                        val bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                        view.imageProfile.setImageBitmap(bitmap)
                        view.progress_bar.visibility=View.INVISIBLE
                    }
            }



        view.imageProfile.setOnClickListener {
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

    // modifica del profilo
    fun changeVisibility() {
        // textView invisibili
        nameSurnameText.visibility = View.INVISIBLE
        emailTextView.visibility = View.INVISIBLE
        dataTextView.visibility = View.INVISIBLE
        // editText visibili
        nameEditText.visibility = View.VISIBLE
        surnameEditText.visibility = View.VISIBLE
        emailEditText.visibility = View.VISIBLE
        dataEditTextView.visibility = View.VISIBLE

        editProfileButton.text = resources.getString(R.string.saveProfile)
        passCancButton.text = resources.getString(R.string.canc_modifica)
        val date = user.dd.toString() + " / " + user.mm.toString() + " / " + user.yy.toString()
        day = user.dd
        month = user.mm
        year = user.yy
        dataEditTextView.text = date
    }

    fun updateProfile() {
        // estrazionde dei dati dai editText
        val name = nameEditText.text.toString().trim()
        val surname = surnameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        var ok: Boolean = true
        var calendar= Calendar.getInstance()
        var data = Calendar.getInstance()
        var limite=Calendar.getInstance()
        limite.set(2008,1,1)
        data.set(year.toInt(),month.toInt()-1,day.toInt())
        Log.d("CALENDAR","data di oggi : ${calendar.time} , data di nascita ${data.time}")


        // controllo se sono vuoti gli editText
        // TODO: CONTROLLARE I MESSAGGI DI ERRORE
        if (ok && name.isEmpty()) {
            nameEditText.error = resources.getString(R.string.passEmpty)
            nameEditText.requestFocus()
            ok = false
        }
        if (ok && surname.isEmpty()) {
            surnameEditText.error = resources.getString(R.string.passEmpty)
            surnameEditText.requestFocus()
            ok = false
        }
        if (ok && email.isEmpty()) {
            emailEditText.error = resources.getString(R.string.emailEmpty)
            emailEditText.requestFocus()
            ok = false
        }
        if (ok && data.after(calendar)){
            dataEditTextView.text="Hai inserito una data nel futuro"
            dataEditTextView.requestFocus()
            ok=false
        }
        if (ok && data.after(limite)){
            dataEditTextView.text="Hai meno di 12 anni"
            dataEditTextView.requestFocus()
            ok=false
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
                        val credential = EmailAuthProvider.getCredential(user.email, password)

                        cUser!!.reauthenticate(credential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                alertDialog.dismiss()
                                val email = emailEditText.text.toString().trim()
                                val newUser = User(name, surname, email, day, month, year)

                                cUser.updateEmail(email).addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        Toast.makeText(activity, "Update del profilo avvenuto con successo", Toast.LENGTH_LONG).show()
                                        usersDB.child(id).setValue(newUser)
                                        user = newUser
                                        updateLayout(newUser)
                                    } else {
                                        Toast.makeText(activity, "ERRORE NELL'UPDATE", Toast.LENGTH_LONG).show()
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

    fun updateLayout(user: User) {
        // textView visibili
        nameSurnameText.visibility = View.VISIBLE
        emailTextView.visibility = View.VISIBLE
        dataTextView.visibility = View.VISIBLE
        // editText invisibili
        nameEditText.visibility = View.INVISIBLE
        surnameEditText.visibility = View.INVISIBLE
        emailEditText.visibility = View.INVISIBLE
        dataEditTextView.visibility = View.INVISIBLE

        nameSurnameText.text = "${user.nome} ${user.cognome}"
        emailTextView.text = user.email
        dataTextView.text = "${user.dd}/${user.mm}/${user.yy}"
        // parte invisibile
        nameEditText.setText(user.nome)
        surnameEditText.setText(user.cognome)
        emailEditText.setText(user.email)
        dataEditTextView.setText(dataTextView.text)

        editProfileButton.text = resources.getString(R.string.modifica_profilo)
        passCancButton.text = resources.getString(R.string.modifica_password)
    }

    fun editPassword(){
        val newPassDialogView = LayoutInflater.from(context).inflate(R.layout.update_password, null)
        val mBuilder = AlertDialog.Builder(context).setView(newPassDialogView)
        val alertDPasswordDialog = mBuilder.show()

        newPassDialogView.okNewPassButton.setOnClickListener {
            var ok = true
            val oldPassString = newPassDialogView.oldPassword.text.toString()
            val newPasswordString = newPassDialogView.newPassword.text.toString()

            if (ok && oldPassString.isEmpty()) {
                newPassDialogView.oldPassword.error = resources.getString(R.string.passEmpty)
                newPassDialogView.oldPassword.requestFocus()
                ok = false
            }
            if (ok && newPasswordString.isEmpty()) {
                newPassDialogView.newPassword.error = resources.getString(R.string.passEmpty)
                newPassDialogView.newPassword.requestFocus()
                ok = false
            }
            if(ok && newPasswordString != newPassDialogView.confirmNewPassword.text.toString()){
                newPassDialogView.confirmNewPassword.error = resources.getString(R.string.confpassDifferent)
                ok = false
            }

            if(ok) {
                currentUser.let { cUser ->
                    val credential = EmailAuthProvider.getCredential(user.email, oldPassString)

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
        dataEditTextView.text = date
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RIC && resultCode == RESULT_OK) {
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
        } else if (requestCode == AGC && resultCode == RESULT_OK && data != null && data.data != null) {

            val image = data.data
            vista!!.progress_bar.visibility=View.VISIBLE
            vista!!.imageProfile.setImageURI(image)
            vista!!.progress_bar.visibility=View.INVISIBLE

            FirebaseStorage.getInstance().reference.child("pics/$id").putFile(image!!)
                .addOnFailureListener {
                    Log.e("UPLOAD FROM GALLERY", it.message)
                }
        }

    }

    class downloadImage: AsyncTask<String,Void,Bitmap>(){
        override fun doInBackground(vararg params: String?): Bitmap? {
            val newURL = URL(params[0])
            return BitmapFactory.decodeStream(newURL.openConnection().getInputStream())
        }
    }

}