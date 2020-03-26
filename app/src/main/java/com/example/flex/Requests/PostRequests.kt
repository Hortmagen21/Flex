package com.example.flex.Requests

import android.content.Context
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

class PostRequests(
    private val url: String,
    private val password: String,
    private val login: String,
    private val email: String,
    private val context: AppCompatActivity?
) {
    val cookieManager = CookieManager()
    private val client: OkHttpClient
    var sessionId = ""
    var csrftoken = ""


    init {
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()
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
            .post(formBody)
            .addHeader(MainData().HEADER_REFRER, "https://" + MainData().BASE_URL)
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
                        context.runOnUiThread {
                            context.setCookies(cookies)
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
            .addPathSegment(MainData().URL_PREFIX_ACC_BASE)
            .addPathSegment("logout").build()
        val request = Request.Builder().url(urlHttp)
            .addHeader(MainData().HEADER_REFRER, "https://" + MainData().BASE_URL)
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
                            editor.putString(MainData().CRSFTOKEN, "")
                            editor.putString(MainData().SESION_ID, "")
                            editor.apply()
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
            .host(MainData().BASE_URL)
            .addPathSegment(MainData().URL_PREFIX_ACC_BASE)
            .addPathSegment("checklog").build()
        val request = Request.Builder().url(urlHttp)
            .addHeader(MainData().HEADER_REFRER, "https://" + MainData().BASE_URL)
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
            .addHeader(MainData().HEADER_REFRER, "https://" + MainData().BASE_URL)
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