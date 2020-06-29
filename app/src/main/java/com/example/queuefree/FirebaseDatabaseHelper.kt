package com.example.queuefree

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class FirebaseDatabaseHelper (path : String) {

    var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val id = FirebaseAuth.getInstance().currentUser!!.uid.trim { it <= ' ' }
    var reference: DatabaseReference = database.getReference(path)
    var user = User("","","","",0, 0, 0, "")

    interface DataStatus{
        fun DataIsLoaded(user: User)
    }

    fun readUserFromDB(ds: DataStatus){
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                    for (uDB in p0.children) { // per tutti gli utente dentro la tabella user
                        if(id == uDB.key){
                            for(prova in uDB.children) {
                                when(prova.key) {
                                    "nome" -> user.nome = prova.value as String
                                    "cognome" -> user.cognome = prova.value as String
                                    "email" -> user.email = prova.value as String
                                    "dd" -> user.dd = prova.value as Long
                                    "mm" -> user.mm = prova.value as Long
                                    "yy" -> user.yy = prova.value as Long
                                    "sesso" -> user.sesso = prova.value as String
                                    "password" -> user.password = prova.value as String
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