package com.example.flex

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SignIn:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        setActionListener()
    }
    fun setActionListener(){
        var signInButton=findViewById<Button>(R.id.sign_in_button)
        signInButton.setOnClickListener{
            if(signInButton.text=="Sign in")
                signInButton.text= 1.toString()
            else
                signInButton.text=(signInButton.text.toString().toInt()+1).toString()
        }
    }
}