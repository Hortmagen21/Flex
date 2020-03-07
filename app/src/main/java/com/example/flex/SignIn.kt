package com.example.flex

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        setActionListener()
    }

    private fun setActionListener() {
        var signInButton = findViewById<Button>(R.id.sign_in_button)
        signInButton.setOnClickListener {
            var login = findViewById<EditText>(R.id.login)
            var password = findViewById<EditText>(R.id.password)
            var signUp = findViewById<Button>(R.id.sign_up_button)
            signUp.setOnClickListener {
                if (password.text.toString() != "" && login.text.toString() != "") {
                    if (login.text.toString().contains("@")) {
                        PostRequests(
                            MainData().BASE_URL + MainData().URL_LOGIN,
                            password.text.toString(), "",
                            login.text.toString()
                        )
                    } else {
                        PostRequests(
                            MainData().BASE_URL + MainData().URL_LOGIN,
                            password.text.toString(),
                            login.text.toString(), ""
                        )
                    }
                } else
                    Toast.makeText(this, "try again", Toast.LENGTH_LONG).show()
            }
        }
    }
}
