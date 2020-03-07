package com.example.flex

import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException

class Registration : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_activity)
        setActionListener()
    }
    private fun setActionListener(){
        var email=findViewById<EditText>(R.id.email)
        var login=findViewById<EditText>(R.id.login)
        var password=findViewById<EditText>(R.id.password)
        var repeatPassword=findViewById<EditText>(R.id.repeat_password)
        var signUp=findViewById<Button>(R.id.sign_up_button)
        signUp.setOnClickListener{
            if (password.text.toString() == repeatPassword.text.toString() &&
                login.text.toString() != "" && email.text.toString() != ""
            ) {
                PostRequests(
                    MainData().BASE_URL + MainData().URL_REGISTRATION,
                    password.text.toString(),
                    login.text.toString(),
                    email.text.toString()
                )
            } else
                Toast.makeText(this, "try again", Toast.LENGTH_LONG).show()
        }

    }
}