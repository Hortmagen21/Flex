package com.example.flex.Requests

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flex.Fragments.CameraFragment
import com.example.flex.MainData
import com.example.flex.SignIn
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy

class UploadFileRequests(private val fragment: Fragment, private val csrftoken: String, private val sessionId: String) {
    private val cookieManager = CookieManager()
    private val client: OkHttpClient

    init {
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()
    }
    fun stopRequests(){
        for(call in client.dispatcher.queuedCalls()){
            if(call.request().tag()==MainData.TAG_UPLOAD){
                call.cancel()
            }
        }
        for(call in client.dispatcher.runningCalls()){
            if(call.request().tag()==MainData.TAG_UPLOAD){
                call.cancel()
            }
        }
    }

    fun uploadRequest(file: File) {
        val formBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "img",
                file.name,
                RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
            )
            .addFormDataPart("csrfmiddlewaretoken", csrftoken)
            .build()
        val request = Request.Builder()
            .tag(MainData.TAG_UPLOAD)
            .url("https://${MainData.BASE_URL}/${MainData.URL_PREFIX_USER_PROFILE}/${MainData.SEND_IMAGE}")
            .post(formBody)
            .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
            .addHeader("Cookie", "csrftoken=$csrftoken; sessionid=$sessionId")
            .build()
        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val isLogined = response.header("isLogin", "true")
                    if (isLogined == "true") {
                        val post = response.body?.string()
                        val jsonObject: JSONObject
                        var map = mutableMapOf<String, String>()
                        if (post != null) {
                            jsonObject = JSONObject(post)
                            map = toMap(jsonObject)
                        }
                        val link = map["src"]
                        if(fragment is CameraFragment){
                            (fragment.context as AppCompatActivity).runOnUiThread {
                                if (link != null) {
                                    fragment.setImage(link)
                                }
                            }
                        }
                    }
                }else  if(response.code==MainData.ERR_403){
                    (fragment.context as AppCompatActivity).runOnUiThread {
                        val intent= Intent(fragment.context as AppCompatActivity, SignIn().javaClass)
                        (fragment.context as AppCompatActivity).startActivity(intent)
                        (fragment.context as AppCompatActivity).finish()
                    }
                } else {

                }
            }
        })
    }


    private fun toMap(json: JSONObject): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            var value = json.get(key)
            if (value is JSONObject) {
                value = toMap(value)
                map.putAll(value)
            } else if (value is JSONArray) {
                value = toList(value)
            } else {
                map[key] = value.toString()
            }
        }
        return map
    }

    private fun toList(json: JSONArray): List<String> {
        val list = mutableListOf<String>()
        for (i in 0..json.length()) {
            var value = json.get(i)
            if (value is JSONObject) {
                value = toMap(value)
            } else if (value is JSONArray) {
                value = toList(value)
                list.addAll(value)
            } else {
                list.add(value.toString())
            }

        }
        return list
    }
}