package com.example.flex.Requests

import android.os.Handler
import android.os.Looper
import com.example.flex.MainData
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpCookie

class RegistRequests(private val mRegistRequestInteraction: RegistRequestInteraction) {
    val cookieManager = CookieManager()
    private val mClient: OkHttpClient
    private val mHandler = Handler(Looper.getMainLooper())
    private var mRunnable: Runnable? = null
    private var mSessionId: String
    private var mCsrftoken: String

    constructor(mRegistRequestInteraction: RegistRequestInteraction,csrftoken: String, sessionId: String) : this(mRegistRequestInteraction) {
        this.mCsrftoken = csrftoken
        this.mSessionId = sessionId
    }

    init {
        this.mCsrftoken = ""
        this.mSessionId = ""
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        mClient = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()
    }

    fun stopRequests() {
        for (call in mClient.dispatcher.queuedCalls()) {
            if (call.request().tag() == MainData.TAG_LOGIN ||
                call.request().tag() == MainData.TAG_LOGOUT ||
                call.request().tag() == MainData.TAG_REGISTER
            ) {
                call.cancel()
            }
        }
        for (call in mClient.dispatcher.runningCalls()) {
            if (call.request().tag() == MainData.TAG_LOGIN ||
                call.request().tag() == MainData.TAG_LOGOUT ||
                call.request().tag() == MainData.TAG_REGISTER
            ) {
                call.cancel()
            }
        }
        mHandler.removeCallbacks(mRunnable)
    }

    fun login(password: String, login: String) {
        var cookies: List<HttpCookie>
        val formBody = FormBody.Builder()
            .add("password", password)
            .add("username", login)
            .add("token", FirebaseInstanceId.getInstance().token ?: "-1")
            .build()
        val request = Request.Builder()
            .url("https://${MainData.BASE_URL}/${MainData.URL_PREFIX_ACC_BASE}/${MainData.LOGIN}")
            .tag(MainData.TAG_LOGIN)
            .post(formBody)
            .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
            .build()

        val call = mClient.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    CoroutineScope(IO).launch {
                        val body = response.body!!.string()
                        cookies = cookieManager.cookieStore.cookies
                        mRegistRequestInteraction.setCSRFToken(cookies[0].value)
                        mRegistRequestInteraction.setSessionId(cookies[1].value)
                        mRegistRequestInteraction.setYourId(body.toLong())
                        mRegistRequestInteraction.notMustSignIn()
                    }
                } else {

                }
            }
        })
    }

    fun logout() {
        var cookies: List<HttpCookie>
        val urlHttp = HttpUrl.Builder().scheme("https")
            .host(MainData.BASE_URL)
            .addPathSegment(MainData.URL_PREFIX_ACC_BASE)
            .addPathSegment(MainData.LOGOUT).build()
        val request = Request.Builder().url(urlHttp)
            .tag(MainData.TAG_LOGOUT)
            .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
            .addHeader("Cookie", "csrftoken=$mCsrftoken; sessionid=$mSessionId")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        val call = mClient.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    CoroutineScope(IO).launch {
                        cookies = cookieManager.cookieStore.cookies
                        mRegistRequestInteraction.setCSRFToken("")
                        mRegistRequestInteraction.setSessionId("")
                        mRegistRequestInteraction.setYourId(0)
                        mRegistRequestInteraction.mustSignIn()
                    }
                } else {

                }
            }
        })
    }

    fun checkLog() {
        var cookies: List<HttpCookie>
        val urlHttp = HttpUrl.Builder().scheme("https")
            .host(MainData.BASE_URL)
            .addPathSegment(MainData.URL_PREFIX_ACC_BASE)
            .addPathSegment(MainData.CHECK_LOG).build()
        val request = Request.Builder().url(urlHttp)
            .tag(MainData.TAG_CHECKLOG)
            .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
            //.addHeader("Authorization",sessionId )
            .addHeader("Cookie", "csrftoken=$mCsrftoken; sessionid=$mSessionId")
            .addHeader("Host", "sleepy-ocean-25130.herokuapp.com")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        val call = mClient.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //cookies.add(HttpCookie("you failed","you failed"))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    cookies = cookieManager.cookieStore.cookies
                } else if (response.code == MainData.ERR_403) {
                    /*context!!.runOnUiThread {
                        val intent = Intent(context, SignIn().javaClass)
                        context.startActivity(intent)
                        context.finish()
                    }*/
                } else {

                }
            }
        })
    }

    fun register(password: String, login: String, email: String) {
        var cookies = mutableListOf<HttpCookie>()
        val formBody = FormBody.Builder()
            .add("password", password)
            .add("username", login)
            .add("email", email)
            .add("token", FirebaseInstanceId.getInstance().token ?: "-1")
            .build()
        val request = Request.Builder()
            .url("https://${MainData.BASE_URL}/${MainData.URL_PREFIX_ACC_BASE}/${MainData.REGISTRATON}")
            .tag(MainData.TAG_REGISTER)
            .post(formBody)
            .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
            .build()
        val call = mClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                cookies.add(HttpCookie("you failed", "you failed"))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    CoroutineScope(IO).launch {
                        cookies = cookieManager.cookieStore.cookies
                        /*repository.setCSRFToken(cookies[0].value)
                        repository.setSessionId(cookies[1].value)*/
                    }
                } else {

                }
            }
        })
    }

    interface RegistRequestInteraction {
        fun setCSRFToken(csrftoken: String)
        fun setSessionId(sessionId: String)
        fun setYourId(id: Long)
        fun mustSignIn()
        fun notMustSignIn()
    }
}