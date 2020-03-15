package com.example.flex

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpCookie

class Registration : AppCompatActivity() {
    lateinit var textView: TextView
    lateinit var textView2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_activity)
        setActionListener()
    }
    private fun setActionListener(){
        textView=findViewById(R.id.textView)
        textView2=findViewById(R.id.textView2)
        val email=findViewById<EditText>(R.id.email)
        val login=findViewById<EditText>(R.id.login)
        val password=findViewById<EditText>(R.id.password)
        val repeatPassword=findViewById<EditText>(R.id.repeat_password)
        val signUp=findViewById<Button>(R.id.sign_up_button)
        signUp.setOnClickListener{
            if (password.text.toString() == repeatPassword.text.toString() &&
                login.text.toString() != "" && email.text.toString() != ""
            ) {

                val request=PostRequests(
                    MainData().BASE_URL + MainData().URL_REGISTRATION,
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
    fun makeToast(string: String?){
        Toast.makeText(this, "header:$string",Toast.LENGTH_LONG).show()
    }
    fun makeCookieToast(cookies:List<HttpCookie>){
        var str=""
        for(cookie in cookies){
            str+="${cookie.name} ${cookie.value} ;\n"
        }
        Toast.makeText(this, "cookie:$str",Toast.LENGTH_LONG).show()
    }
}