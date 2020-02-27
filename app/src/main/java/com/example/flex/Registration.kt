package com.example.flex

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_activity)
        setActionListener()
    }
    fun setActionListener(){
        var signUp=findViewById<Button>(R.id.sign_up_button)
        signUp.setOnClickListener{
            if(signUp.text=="Sign up")
                signUp.text= 1.toString()
            else
                signUp.text=(signUp.text.toString().toInt()+1).toString()
        }
    }
}