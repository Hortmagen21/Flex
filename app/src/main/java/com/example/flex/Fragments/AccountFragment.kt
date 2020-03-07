package com.example.flex.Fragments

import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.flex.POJO.User
import com.example.flex.R
import com.example.flex.Registration
import com.example.flex.SignIn
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account.*
import java.lang.RuntimeException

class AccountFragment : Fragment() {
    private lateinit var avatar: ImageView
    private lateinit var followedCount: TextView
    private lateinit var followersCount: TextView
    private var user:User?=null
    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_account, container, false)
        avatar=v.findViewById(R.id.user_icon_main)
        followedCount=v.findViewById(R.id.followed_count)
        followersCount=v.findViewById(R.id.followers_count)
        if(user!=null){
            Picasso.get().load(user!!.imageUrl).into(avatar)
            followersCount.text=user!!.followersCount.toString()
            followedCount.text=user!!.followingCount.toString()
        }
        addActionListener()
        return v
    }

    private fun addActionListener() {
        var btnRegistration = v.findViewById<Button>(R.id.button_registration)
        var btnLogIn = v.findViewById<Button>(R.id.button_login)
        btnRegistration.setOnClickListener {
            var intent = Intent(v.context, Registration().javaClass)
            startActivity(intent)
        }
        btnLogIn.setOnClickListener {
            var intent = Intent(v.context, SignIn().javaClass)
            startActivity(intent)
        }
    }
    fun setUser(user:User){
        this.user=user
    }
}

