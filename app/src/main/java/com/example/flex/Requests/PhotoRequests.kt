package com.example.flex.Requests

import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flex.Fragments.CameraFragment
import com.example.flex.MainData
import com.example.flex.SignIn
import okhttp3.*
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy

class PhotoRequests(
    private val fragment: Fragment,
    private val csrftoken: String,
    private val sessionId: String
) {
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
            if(call.request().tag()==MainData.TAG_DOWNLOAD_PHOTO&&
                call.request().tag()==MainData.TAG_VIEW_PHOTO){
                call.cancel()
            }
        }
        for(call in client.dispatcher.runningCalls()){
            if(call.request().tag()==MainData.TAG_DOWNLOAD_PHOTO&&
                        call.request().tag()==MainData.TAG_VIEW_PHOTO){
                call.cancel()
            }
        }
    }
    fun viewPhoto(link: String) {
        val call = makeGetPhotoCall(link,MainData.TAG_VIEW_PHOTO)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val inputStream = response.body!!.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    if (fragment is CameraFragment) {
                        (fragment.context as AppCompatActivity).runOnUiThread {
                            fragment.image.setImageBitmap(bitmap)
                        }
                    }
                } else if (response.code == MainData.ERR_403) {
                    (fragment.context as AppCompatActivity).runOnUiThread {
                        val intent =
                            Intent(fragment.context as AppCompatActivity, SignIn().javaClass)
                        (fragment.context as AppCompatActivity).startActivity(intent)
                        (fragment.context as AppCompatActivity).finish()
                    }
                } else {

                }
            }
        })
    }

    fun downloadPhotoByUrl(url: String, photoView: ImageView) {
        val call = makeGetPhotoCall(url,MainData.TAG_DOWNLOAD_PHOTO)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val inputStream = response.body!!.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    (fragment.context as AppCompatActivity).runOnUiThread {
                        photoView.setImageBitmap(bitmap)
                    }

                } else if (response.code == MainData.ERR_403) {
                    (fragment.context as AppCompatActivity).runOnUiThread {
                        val intent =
                            Intent(fragment.context as AppCompatActivity, SignIn().javaClass)
                        (fragment.context as AppCompatActivity).startActivity(intent)
                        (fragment.context as AppCompatActivity).finish()
                    }
                }
            }
        })
    }

    private fun makeGetPhotoCall(link: String, tag: String = ""): Call {
        val urlHttp = HttpUrl.Builder().scheme("https")
            .host(MainData.BASE_URL)
            .addPathSegment(MainData.URL_PREFIX_USER_PROFILE)
            .addPathSegment(MainData.VIEW_PHOTO)
            .addQueryParameter("img", link)
            .build()
        val request =
            if (tag == "") {
                Request.Builder().url(urlHttp)
                    .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
                    .addHeader("Cookie", "csrftoken=$csrftoken; sessionid=$sessionId")
                    .build()
            } else {
                Request.Builder().url(urlHttp)
                    .tag(tag)
                    .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
                    .addHeader("Cookie", "csrftoken=$csrftoken; sessionid=$sessionId")
                    .build()
            }
        return client.newCall(request)
    }
}
