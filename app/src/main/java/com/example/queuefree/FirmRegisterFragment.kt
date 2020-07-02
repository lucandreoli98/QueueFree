package com.example.queuefree

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.fragment_register_firm.*
import kotlinx.android.synthetic.main.fragment_register_firm.view.*
import kotlinx.android.synthetic.main.fragment_register_firm.view.placesSelect
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class FirmRegisterFragment : Fragment() {

    private var fireBase: FirebaseAuth? = null
    private var locationString: String = ""
    private var locationID: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register_firm, container, false) // in kotlin tutte le id vengono racchiuse nella view

        fireBase = FirebaseAuth.getInstance()
        var positionSpinner :Int = 0
        var categoriaString: String =""

        //Per autocomplete su luoghi
        Places.initialize(activity!!,resources.getString(R.string.google_map_api_key))
        view.placesSelect.setOnClickListener{
            val list = listOf(Place.Field.ADDRESS,Place.Field.NAME)
            val intent: Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,list)
                .setCountry("IT")
                .build(activity!!)
            startActivityForResult(intent,100)
        }


        // Per lo spinner
        ArrayAdapter.createFromResource(
            activity!!,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            view.firm_category.adapter = adapter
        }

        view.firm_category.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                val categorie = resources.getStringArray(R.array.category_array)
                positionSpinner=position
                categoriaString=categorie[positionSpinner]
                (parent.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                (parent.getChildAt(0) as TextView).textSize = 17.5F

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        view.submitButton.setOnClickListener {

            val nome = nameRegisterEditText.text.toString().trim()
            val cognome = surnameRegisterEditText.text.toString().trim()
            val email = emailRegisterEditText.text.toString().trim()
            val password = passRegisterEditText.text.toString().trim()
            val conf = confpassRegisterEditText.text.toString().trim()
            val firm_n = firm_name.text.toString().trim()
            val placeString = placesSelect.text.toString().trim()
            var ok=true          //ti evita di fare le operazioni in piu per ogni controllo basta che un campo non sia compilato

            // verifica se tutti i campi sono stati compilati, altrimenti segnala un errore
            if (ok && nome.isEmpty()) {
                nameRegisterEditText.error = resources.getString(R.string.nameEmpty)
                nameRegisterEditText.requestFocus()
                ok=false
            }
            if (ok && cognome.isEmpty()) {
                surnameRegisterEditText.error = resources.getString(R.string.surnameEmpty)
                surnameRegisterEditText.requestFocus()
                ok=false
            }
            if (ok && email.isEmpty()) {
                emailRegisterEditText.error = resources.getString(R.string.emailEmpty)
                emailRegisterEditText.requestFocus()
                ok=false
            }
            if (ok && password.isEmpty()) {
                passRegisterEditText.error = resources.getString(R.string.passEmpty)
                passRegisterEditText.requestFocus()
                ok=false
            }
            if (ok && conf.isEmpty()) {
                confpassRegisterEditText.error = resources.getString(R.string.confpassEmpty)
                confpassRegisterEditText.requestFocus()
                ok=false
            }
            if (ok && password != conf) {
                confpassRegisterEditText.error = resources.getString(R.string.confpassDifferent)
                confpassRegisterEditText.requestFocus()
                ok=false
            }
            if (ok && firm_n.isEmpty()) {
                firm_name.error = resources.getString(R.string.f_nameEmpty)
                firm_name.requestFocus()
                ok=false
            }
            if(ok && placeString.isEmpty()){
                placesSelect.error = resources.getString(R.string.placesnameEmpty)
                placesSelect.requestFocus()
                ok=false
            }
            if(ok && positionSpinner==0){
                Toast.makeText(activity!!,resources.getString(R.string.categoryEmpty),Toast.LENGTH_SHORT).show()
                ok=false
            }


            if(ok)
                fireBase!!.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val f = Firm(
                                nome,
                                cognome,
                                email,
                                password,
                                firm_n,
                                categoriaString,
                                locationString,
                                locationID
                            )

                            Log.e("task successful", resources.getString(R.string.userRegistrated))

                            val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
                            FirebaseDatabase.getInstance().getReference("/firm/$id").setValue(f)
                            FirebaseAuth.getInstance().currentUser!!.sendEmailVerification()
                            fireBase!!.signOut()

                            fragmentManager!!.beginTransaction()
                                .replace(R.id.login_fragment, LoginFragment()).commit()
                        } else {
                            Log.e("task not successful: ", task.exception.toString())
                        }
                    }
                    .addOnFailureListener{
                        Toast.makeText(activity!!,it.message,Toast.LENGTH_SHORT).show()
                    }
        }






        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == AutocompleteActivity.RESULT_OK){
            val place: Place = Autocomplete.getPlaceFromIntent(data!!)

            placesSelect.text = place.address
            locationID= place.id.toString()
            locationString= place.address.toString()


        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            val status: Status = Autocomplete.getStatusFromIntent(data!!)

            Toast.makeText(activity!!,status.statusMessage,Toast.LENGTH_SHORT).show()
        }else if(resultCode == AutocompleteActivity.RESULT_CANCELED){
            placesSelect.setText("")
        }

    }
}