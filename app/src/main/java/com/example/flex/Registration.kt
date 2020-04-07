package com.example.flex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flex.Requests.RegistRequests
import java.net.HttpCookie

class Registration : AppCompatActivity() {
    lateinit var email:EditText
    lateinit var login:EditText
    lateinit var password:EditText
    lateinit var repeatPassword:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_activity)
        setActionListener()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        email.setText("")
        login.setText("")
        password.setText("")
        repeatPassword.setText("")
    }

    private fun setActionListener() {
        email = findViewById(R.id.email)
        login = findViewById(R.id.login)
        password = findViewById(R.id.password)
        repeatPassword = findViewById(R.id.repeat_password)
        val signUp = findViewById<Button>(R.id.sign_up_button)
        val haveAcc=findViewById<TextView>(R.id.have_acc)
        haveAcc.setOnClickListener {
            val intent=Intent(this,SignIn().javaClass)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
        signUp.setOnClickListener {
            if (password.text.toString() == repeatPassword.text.toString() &&
                login.text.toString() != "" && email.text.toString().contains("@gmail.com")
            ) {

                val request = RegistRequests(
                    "https://${MainData.BASE_URL}/${MainData.URL_PREFIX_ACC_BASE}/${MainData.REGISTRATON}",
                    password.text.toString(),
                    login.text.toString(),
                    email.text.toString(),
                    this
                )
                request.callRegister()
            } else
                Toast.makeText(this, "try again", Toast.LENGTH_LONG).show()
        }
    }

    fun setCookies(cookies: List<HttpCookie>) {
        val sharedPreferences = getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        /*val editor = sharedPreferences.edit()
        editor.putString(MainData().CRSFTOKEN, cookies[0].value)
        editor.putString(MainData().SESION_ID, cookies[1].value)
        editor.apply()*/
        val intent=Intent(this,MainActivity().javaClass)
        startActivity(intent)
        finish()
    }
}