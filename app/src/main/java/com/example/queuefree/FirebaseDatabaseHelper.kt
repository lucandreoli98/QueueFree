package com.example.queuefree

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class FirebaseDatabaseHelper () {

    var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
    var reference: DatabaseReference = database.getReference("users")
    var user = User("","","",0L, 0L, 0L)

    interface DataStatus{
        fun DataIsLoaded(user : User)
    }

    fun readUserFromDB(ds: DataStatus){
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                    for (uDB in p0.children) { // per tutti gli utente dentro la tabella user
                        if(id == uDB.key){
                            for(field in uDB.children) {
                                when(field.key) {
                                    "nome" -> user.nome = field.value as String
                                    "cognome" -> user.cognome = field.value as String
                                    "email" -> user.email = field.value as String
                                    "dd" -> user.dd = field.value as Long
                                    "mm" -> user.mm = field.value as Long
                                    "yy" -> user.yy = field.value as Long
                                }
                            }
                            ds.DataIsLoaded(user)
                        }
                    }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("OnCancelled", p0.toException().toString())
            }
        })
    }
}