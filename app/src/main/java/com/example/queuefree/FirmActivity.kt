package com.example.queuefree

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_show_profile.*
import kotlinx.android.synthetic.main.remove_firm.view.*
import kotlinx.android.synthetic.main.remove_firm.view.cancRemoveButton
import kotlinx.android.synthetic.main.remove_firm_identity.view.*

class FirmActivity : AppCompatActivity() {

    private val fb = FirebaseDatabaseHelper()
    private val mContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firm_profile)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_firm, FirmFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu,menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout      -> signOut()
            R.id.menu_remove_firm -> removeFirm()
        }

        return true
    }

    fun signOut(){
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        val i=Intent(this, MainActivity::class.java)
        i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }

    fun removeFirm(){
        val messageDialogView = LayoutInflater.from(this).inflate(R.layout.remove_firm, null)
        val mBuilder = AlertDialog.Builder(this).setView(messageDialogView)
        var alertDialog = mBuilder.show()

        messageDialogView.cancRemoveButton.setOnClickListener {
            alertDialog.dismiss()
            Log.d("CANCEL: ","premuto")
        }

        messageDialogView.removeButton.setOnClickListener {
            alertDialog.dismiss()
            val removeFirmDialogView = LayoutInflater.from(this).inflate(R.layout.remove_firm_identity,null)
            val builderRemove = AlertDialog.Builder(this).setView(removeFirmDialogView)
            val removeDialog = builderRemove.show()

            removeFirmDialogView.cancPassRemoveButton.setOnClickListener {
                removeDialog.dismiss()
            }

            removeFirmDialogView.okRemoveButton.setOnClickListener {
                val email = removeFirmDialogView.emailRemoveEditText.text.toString()
                val password = removeFirmDialogView.passwordRemoveEditText.text.toString()
                var isCorrect = true

                if(email.isEmpty()){
                    removeFirmDialogView.emailRemoveEditText.error = resources.getString(R.string.emailEmpty)
                    removeFirmDialogView.emailRemoveEditText.requestFocus()
                    isCorrect = false
                }
                if(password.isEmpty()){
                    removeFirmDialogView.passwordRemoveEditText.error = resources.getString(R.string.passEmpty)
                    removeFirmDialogView.passwordRemoveEditText.requestFocus()
                    isCorrect = false
                }
                if(isCorrect){
                    FirebaseAuth.getInstance().currentUser.let { cUser ->
                        val credential = EmailAuthProvider.getCredential(email, password)

                        cUser!!.reauthenticate(credential).addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                FirebaseDatabase.getInstance().getReference("bookings/${cUser.uid}").removeValue()
                                FirebaseDatabase.getInstance().getReference("firm/${cUser.uid}").removeValue()
                                cUser.delete().addOnCompleteListener { taskDelete ->
                                    if(taskDelete.isSuccessful)
                                        startActivity(Intent(mContext,MainActivity::class.java))
                                    else
                                        Log.e("Error: ", taskDelete.exception.toString())
                                }
                            }
                            else
                                Toast.makeText(mContext, "Email e/o password errate", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
/*fb.readFirmFromDB(object : FirebaseDatabaseHelper.DataStatusFirm{
                    override fun DataisLoadedFirm(firm: Firm) {
                        fb.removeFirm(mContext,firm,object : FirebaseDatabaseHelper.DataStatusCancel{
                            override fun isDeleted(mContext: Context) {
                                currentUser.let { cUser ->
                                    val credential = EmailAuthProvider.getCredential(user.email, password)

                                    cUser!!.reauthenticate(credential).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            alertDialog.dismiss()

                                startActivity(Intent(mContext, MainActivity::class.java))
                            }
                        })
                    }
                })*/