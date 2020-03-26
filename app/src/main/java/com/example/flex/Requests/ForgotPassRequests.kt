package com.example.flex.Requests

import androidx.appcompat.app.AppCompatActivity
import com.example.flex.ForgotPass
import com.example.flex.MainData
import okhttp3.*
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy

class ForgotPassRequests(private val activity: AppCompatActivity) {
    private val client: OkHttpClient
    private val cookieManager = CookieManager()

    init {
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()
    }

    fun forgotPass(email: String) {
        val formBody = FormBody.Builder()
            .add("email", email)
            .build()
        val request =
            Request.Builder().url("https://" + MainData().BASE_URL + MainData().URL_FOGOT_PASS)
                .post(formBody)
                .addHeader(MainData().HEADER_REFRER, "https://" + MainData().BASE_URL)
                .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    if (activity is ForgotPass) {
                        activity.runOnUiThread {
                            activity.onForgotPass()
                        }
                    }
                } else {

                }
            }
        })
    }

    fun changePass(email: String, newPass: String, checkCode: String) {
        val formBody = FormBody.Builder()
            .add("email", email)
            .add("new_password", newPass)
            .add("user_token", checkCode)
            .build()
        val request =
            Request.Builder().url("https://" + MainData().BASE_URL + MainData().URL_CHANGE_PASS)
                .post(formBody)
                .addHeader(MainData().HEADER_REFRER, "https://" + MainData().BASE_URL)
                .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    if (activity is ForgotPass) {
                        activity.runOnUiThread {

                        }
                    }
                } else {

                }
            }
        })
    }
}