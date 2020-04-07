package com.example.flex.Requests

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.flex.MainActivity
import com.example.flex.MainData
import com.example.flex.Registration
import com.example.flex.SignIn
import okhttp3.*
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpCookie

class RegistRequests(
    private val url: String,
    private val password: String,
    private val login: String,
    private val email: String,
    private val context: AppCompatActivity?
) {
    val cookieManager = CookieManager()
    private val client: OkHttpClient
    private var csrftoken: String
    private var sessionId: String

    constructor(
        url: String,
        password: String,
        login: String,
        email: String,
        context: AppCompatActivity?, csrftoken: String, sessionId: String
    ) : this(url, password, login, email, context) {
        this.csrftoken = csrftoken
        this.sessionId = sessionId
    }


    init {
        this.csrftoken = ""
        this.sessionId = ""
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()
    }
    fun stopRequests(){
        for(call in client.dispatcher.queuedCalls()){
            if(call.request().tag()==MainData.TAG_LOGIN&&
                call.request().tag()==MainData.TAG_LOGOUT&&
                call.request().tag()==MainData.TAG_REGISTER){
                call.cancel()
            }
        }
        for(call in client.dispatcher.runningCalls()){
            if(call.request().tag()==MainData.TAG_LOGIN&&
                call.request().tag()==MainData.TAG_LOGOUT&&
                call.request().tag()==MainData.TAG_REGISTER){
                call.cancel()
            }
        }
    }

    fun callLogin() {
        login(url, password, login)
    }

    fun callRegister() {
        register(url, password, login, email)
    }

    private fun login(url: String, password: String, login: String) {
        var cookies: List<HttpCookie>
        val formBody = FormBody.Builder()
            .add("password", password)
            .add("username", login)
            .build()
        val request = Request.Builder().url(url)
            .tag(MainData.TAG_LOGIN)
            .post(formBody)
            .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //cookies.add(HttpCookie("you failed","you failed"))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    cookies = cookieManager.cookieStore.cookies
                    val id = response.body?.string()
                    if (context is SignIn) {
                        context.runOnUiThread {
                            if (id != null) {
                                context.setCookies(cookies, id.toLong())
                            }
                        }
                    }
                } else {

                }
            }
        })
    }

    fun logout() {
        var cookies: List<HttpCookie>
        val urlHttp = HttpUrl.Builder().scheme("https")
            .host("sleepy-ocean-25130.herokuapp.com")
            .addPathSegment(MainData.URL_PREFIX_ACC_BASE)
            .addPathSegment("logout").build()
        val request = Request.Builder().url(urlHttp)
            .tag(MainData.TAG_LOGOUT)
            .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
            .addHeader("Cookie", "csrftoken=$csrftoken; sessionid=$sessionId")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    cookies = cookieManager.cookieStore.cookies
                    if (context is MainActivity) {
                        context.runOnUiThread {
                            val sharedPreferences =
                                context.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString(MainData.CRSFTOKEN, "")
                            editor.putString(MainData.SESION_ID, "")
                            editor.apply()
                            val intent = Intent(context, SignIn().javaClass)
                            context.startActivity(intent)
                            context.finish()

                        }
                    }
                } else {

                }
            }
        })
    }

    fun callCheckLog() {
        checkLog()
    }

    private fun checkLog() {
        var cookies: List<HttpCookie>
        val urlHttp = HttpUrl.Builder().scheme("https")
            .host(MainData.BASE_URL)
            .addPathSegment(MainData.URL_PREFIX_ACC_BASE)
            .addPathSegment(MainData.CHECK_LOG).build()
        val request = Request.Builder().url(urlHttp)
            .tag(MainData.TAG_CHECKLOG)
            .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
            //.addHeader("Authorization",sessionId )
            .addHeader("Cookie", "csrftoken=$csrftoken; sessionid=$sessionId")
            .addHeader("Host", "sleepy-ocean-25130.herokuapp.com")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //cookies.add(HttpCookie("you failed","you failed"))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    cookies = cookieManager.cookieStore.cookies
                } else if (response.code == MainData.ERR_403) {
                    context!!.runOnUiThread {
                        val intent = Intent(context, SignIn().javaClass)
                        context.startActivity(intent)
                        context.finish()
                    }
                } else {

                }
            }
        })
    }

    private fun register(url: String, password: String, login: String, email: String) {
        var cookies = mutableListOf<HttpCookie>()
        val formBody = FormBody.Builder()
            .add("password", password)
            .add("username", login)
            .add("email", email)
            .build()
        val request = Request.Builder().url(url)
            .tag(MainData.TAG_REGISTER)
            .post(formBody)
            .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                cookies.add(HttpCookie("you failed", "you failed"))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    cookies = cookieManager.cookieStore.cookies
                    var str = ""
                    for (cookie in cookies) {
                        str += " ${cookie.name} ${cookie.value} ;"
                    }
                    if (context is Registration) {
                        context.runOnUiThread {
                            context.setCookies(cookies)
                        }
                    }
                } else {

                }
            }
        })
    }


}