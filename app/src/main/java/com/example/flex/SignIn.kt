package com.example.flex

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException
import java.net.HttpCookie

class SignIn : AppCompatActivity() {
    lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        setActionListener()
    }

    private fun setActionListener() {
        val signInButton = findViewById<Button>(R.id.sign_in_button)
        val login = findViewById<EditText>(R.id.login)
        val password = findViewById<EditText>(R.id.password)
        signInButton.setOnClickListener {
            if (password.text.toString() != "" && login.text.toString() != "") {
                val request = PostRequests(
                    MainData().BASE_URL + MainData().URL_LOGIN,
                    password.text.toString(), login.text.toString(),
                    "",
                    this
                )
                request.callLogin()
            } else
                Toast.makeText(this, "try again", Toast.LENGTH_LONG).show()
        }
    }

    fun setCookies(cookies: List<HttpCookie>) {
        val sharedPreferences=getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.putString(MainData().CRSFTOKEN,cookies[0].value)
        editor.putString(MainData().SESION_ID,cookies[1].value)
        editor.apply()
    }
}
