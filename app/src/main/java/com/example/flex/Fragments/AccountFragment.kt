package com.example.flex.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flex.*
import com.example.flex.POJO.User
import com.squareup.picasso.Picasso

class AccountFragment : Fragment() {
    private lateinit var avatar: ImageView
    private lateinit var followedCount: TextView
    private lateinit var followersCount: TextView
    private var user: User? = null
    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_account, container, false)
        avatar = v.findViewById(R.id.user_icon_main)
        followedCount = v.findViewById(R.id.followed_count)
        followersCount = v.findViewById(R.id.followers_count)
        if (user != null) {
            Picasso.get().load(user!!.imageUrl).into(avatar)
            followersCount.text = user!!.followersCount.toString()
            followedCount.text = user!!.followingCount.toString()
        }
        addActionListener()
        return v
    }

    private fun addActionListener() {
        val btnRegistration = v.findViewById<Button>(R.id.button_registration)
        val btnLogIn = v.findViewById<Button>(R.id.button_login)
        val btnLogout = v.findViewById<Button>(R.id.button_logout)
        val btnCheckLog = v.findViewById<Button>(R.id.button_checkLog)
        btnCheckLog.setOnClickListener {
            val request = PostRequests(
                MainData().BASE_URL + MainData().URL_LOGOUT,
                "", "",
                "",
                null
            )
            val activity=v.context as AppCompatActivity
            val sharedPreferences=activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
            request.sessionId=sharedPreferences.getString(MainData().SESION_ID,"")
            request.callCheckLog()
        }
        btnLogout.setOnClickListener {
            val request = PostRequests(
                MainData().BASE_URL + MainData().URL_LOGOUT,
                "", "",
                "",
                null
            )
            request.callLogout()
        }
        btnRegistration.setOnClickListener {
            val intent = Intent(v.context, Registration().javaClass)
            startActivity(intent)
        }
        btnLogIn.setOnClickListener {
            val intent = Intent(v.context, SignIn().javaClass)
            startActivity(intent)
        }
    }

    fun setUser(user: User) {
        this.user = user
    }
}

