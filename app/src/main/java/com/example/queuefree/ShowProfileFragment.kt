package com.example.queuefree

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_show_profile.*
import kotlinx.android.synthetic.main.fragment_show_profile.view.*

class ShowProfileFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_show_profile, container, false)

        val fb : FirebaseDatabaseHelper = FirebaseDatabaseHelper()

        fb!!.readUserFromDB(object : FirebaseDatabaseHelper.DataStatus {
            override fun DataIsLoaded(user: User) {
                nameSurnameText.setText("${user.nome} ${user.cognome}")
                emailTextView.setText(user.email)
                dataTextView.setText("${user.dd}/${user.mm}/${user.yy}")

                view.editButton.setOnClickListener {
                    val args = Bundle()
                    args.putString("nome", user.nome)
                    args.putString("cognome", user.cognome)
                    args.putString("email", user.email)
                    args.putLong("dd", user.dd)
                    args.putLong("mm", user.mm)
                    args.putLong("yy", user.yy)
                    args.putString("password",user.password)

                    val editFragment = EditProfileFragment()
                    editFragment.arguments = args
                    fragmentManager!!.beginTransaction().replace(R.id.profileContainer, editFragment)
                        .commit()
                }
            }
        })

        return view
    }
}
