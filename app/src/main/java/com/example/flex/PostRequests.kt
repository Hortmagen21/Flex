package com.example.flex

import okhttp3.*
import java.io.IOException

class PostRequests {
    private val password: String
    private val url: String
    private val login: String
    private val email: String
    private val client = OkHttpClient()

    constructor(url: String, password: String, login: String, email: String) {
        this.password = password
        this.url = url
        this.login = login
        this.email = email
    }
    fun callLogin(){
        login(url,password,login,email)
    }
    fun callRegister() {
        register(url, password, login, email)
    }

    private fun login(url: String, password: String, login: String, email: String) {
        var formBody = FormBody.Builder()
            .add("password", password)
            .add("username", if (login != "") login else email)
            .build()
        var request = Request.Builder().url(url)
            .post(formBody)
            .addHeader(MainData().HEADER_REFRER, MainData().BASE_URL)
            .build()
        var call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {

                } else {

                }
            }
        })
    }

    private fun register(url: String, password: String, login: String, email: String) {
        var formBody = FormBody.Builder()
            .add("password", password)
            .add("username", login)
            .add("email", email)
            .build()
        var request = Request.Builder().url(url)
            .post(formBody)
            .addHeader(MainData().HEADER_REFRER, MainData().BASE_URL)
            .build()
        var call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {

                } else {

                }
            }
        })
    }

}