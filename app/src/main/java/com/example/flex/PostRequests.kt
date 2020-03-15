package com.example.flex

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpCookie

class PostRequests {
    private val password: String
    private val url: String
    private val login: String
    private val email: String
    val cookieManager = CookieManager()
    private val client: OkHttpClient
    private val context: AppCompatActivity?
    var sessionId=""


    constructor(
        url: String,
        password: String,
        login: String,
        email: String,
        context: AppCompatActivity?
    ) {
        this.password = password
        this.url = url
        this.login = login
        this.email = email
        this.context = context
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager)).build()
    }

    fun callLogout(){
        logout()
    }
    fun callLogin() {
        login(url, password, login)
    }

    fun callRegister() {
        register(url, password, login, email)
    }

    private fun login(url: String, password: String, login: String) {
        var cookies: List<HttpCookie>
        val urlHttp = HttpUrl.Builder().scheme("https")
            .host("sleepy-ocean-25130.herokuapp.com")
            .addPathSegment("login")
            .addQueryParameter("password", password)
            .addQueryParameter("username", login).build()
        val request = Request.Builder().url(urlHttp)
            .addHeader(MainData().HEADER_REFRER, MainData().BASE_URL)
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //cookies.add(HttpCookie("you failed","you failed"))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    cookies = cookieManager.cookieStore.cookies
                    if (context is SignIn) {
                        context.setCookies(cookies)
                    }
                } else {

                }
            }
        })
    }
    private fun logout() {
        var cookies: List<HttpCookie>
        val urlHttp = HttpUrl.Builder().scheme("https")
            .host("sleepy-ocean-25130.herokuapp.com")
            .addPathSegment("logout").build()
        val request = Request.Builder().url(urlHttp)
            .addHeader(MainData().HEADER_REFRER, MainData().BASE_URL)
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //cookies.add(HttpCookie("you failed","you failed"))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    cookies = cookieManager.cookieStore.cookies

                } else {

                }
            }
        })
    }

    fun callCheckLog(){
        checkLog()
    }
    private fun checkLog() {
        var cookies: List<HttpCookie>
        val urlHttp = HttpUrl.Builder().scheme("https")
            .host("sleepy-ocean-25130.herokuapp.com")
            .addPathSegment("checklog").build()
        val request = Request.Builder().url(urlHttp)
            .addHeader(MainData().HEADER_REFRER, MainData().BASE_URL)
            .addHeader("Authorization",sessionId )
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //cookies.add(HttpCookie("you failed","you failed"))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    cookies = cookieManager.cookieStore.cookies

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
            .post(formBody)
            .addHeader(MainData().HEADER_REFRER, MainData().BASE_URL)
            .build()
        val call = client.newCall(request)
        var cook: String? = ""
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
                            context.textView.text = str
                            context.makeToast(str)
                        }
                    }
                    cook = response.headers["Set-Cookie"]
                    if (context is Registration) {
                        context.runOnUiThread {
                            context.textView2.text = cook
                        }
                    }
                } else {

                }
            }
        })
    }

}