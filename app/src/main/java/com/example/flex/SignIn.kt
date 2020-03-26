package com.example.flex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flex.Requests.PostRequests
import java.net.HttpCookie

class SignIn : AppCompatActivity() {
    lateinit var login:EditText
    lateinit var password:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        setActionListener()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        login.setText("")
        password.setText("")
    }

    private fun setActionListener() {
        val signInButton = findViewById<Button>(R.id.sign_in_button)
        login = findViewById(R.id.login)
        password = findViewById(R.id.password)
        val dontAcc=findViewById<TextView>(R.id.dont_acc)
        dontAcc.setOnClickListener{
            val intent= Intent(this,Registration().javaClass)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
        signInButton.setOnClickListener {
            if (password.text.toString() != "" && login.text.toString() != "") {
                val request = PostRequests(
                    "https://"+MainData().BASE_URL + MainData().URL_LOGIN,
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
        val intent=Intent(this,MainActivity().javaClass)
        startActivity(intent)
        finish()
    }
}
