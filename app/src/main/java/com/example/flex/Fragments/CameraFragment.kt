package com.example.flex.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flex.*
import com.example.flex.Requests.PostRequests

class CameraFragment : Fragment() {
    lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_photo, container, false)
        addActionListener()
        return v
    }

    fun addActionListener() {

        val btnRegistration = v.findViewById<Button>(R.id.button_registration)
        val btnLogIn = v.findViewById<Button>(R.id.button_login)
        val btnLogout = v.findViewById<Button>(R.id.button_logout)
        val btnCheckLog = v.findViewById<Button>(R.id.button_checkLog)
        val changePassBtn = v.findViewById<Button>(R.id.button_change_pass)
        btnCheckLog.setOnClickListener {
            val request = PostRequests(
                "https://" + MainData().BASE_URL + MainData().URL_LOGOUT,
                "", "",
                "",
                null
            )
            val activity = v.context as AppCompatActivity
            val sharedPreferences =
                activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
            request.sessionId = sharedPreferences.getString(MainData().SESION_ID, "")
            request.csrftoken = sharedPreferences.getString(MainData().CRSFTOKEN, "")
            request.callCheckLog()
        }
        btnLogout.setOnClickListener {
            val request = PostRequests(
                "https://" + MainData().BASE_URL + MainData().URL_LOGOUT,
                "", "",
                "",
                v.context as AppCompatActivity
            )
            val activity = v.context as AppCompatActivity
            val sharedPreferences =
                activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
            request.sessionId = sharedPreferences.getString(MainData().SESION_ID, "")
            request.csrftoken = sharedPreferences.getString(MainData().CRSFTOKEN, "")
            request.logout()
        }
        changePassBtn.setOnClickListener {
            val intent = Intent(v.context, ForgotPass().javaClass)
            startActivity(intent)
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
}