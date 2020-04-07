package com.example.flex

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.flex.Requests.ForgotPassRequests

class ForgotPass : AppCompatActivity() {
    private lateinit var emailText: EditText
    private lateinit var newPass: EditText
    private lateinit var emailCode: EditText
    private lateinit var forgotPassBtn: Button
    private lateinit var newPassBtn: Button
    private var request:ForgotPassRequests?=null
    private val sharedPreferences =getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)
        setActionListener()
        if(sharedPreferences.getBoolean("isEnabled",false)){
            onForgotPass()
            emailCode.setText(sharedPreferences.getString("email code",""))
            newPass.setText(sharedPreferences.getString("new password",""))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(request!=null){
            request!!.stopRequests()
        }
    }

    private fun setActionListener() {
        emailText = findViewById(R.id.forgot_pass_email)
        forgotPassBtn = findViewById(R.id.forgot_pass_button)
        emailCode = findViewById(R.id.change_pass_code)
        newPass = findViewById(R.id.change_pass_pass)
        newPassBtn = findViewById(R.id.change_pass_button)
        forgotPassBtn.setOnClickListener {
            val request = ForgotPassRequests(this)
            request.forgotPass(emailText.text.toString())
        }
        newPassBtn.setOnClickListener {
            request = ForgotPassRequests(this)
            request!!.changePass(
                emailText.text.toString(),
                newPass.text.toString(),
                emailCode.text.toString()
            )
        }
    }

    fun onForgotPass() {
        val editor=sharedPreferences.edit()
        editor.putBoolean("isEnabled",true)
        editor.apply()
        emailCode.visibility = View.VISIBLE
        newPass.visibility = View.VISIBLE
        newPassBtn.visibility = View.VISIBLE
    }

    fun onChangePass() {

    }
}