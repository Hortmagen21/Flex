package com.example.flex.Requests

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flex.Fragments.AccountFragment
import com.example.flex.Fragments.AccountPostTableRecyclerFragment
import com.example.flex.MainData
import com.example.flex.POJO.Post
import com.example.flex.SignIn
import kotlinx.android.synthetic.main.fragment_account.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpCookie
import java.util.jar.Manifest

class UsersRequests(
    private val fragment: Fragment,
    private val csrftoken: String,
    private val sessionId: String
) {
    private val client: OkHttpClient
    private val cookieManager = CookieManager()

    init {
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()
    }

    fun stopRequests(){
        for(call in client.dispatcher.queuedCalls()){
            if(call.request().tag()==MainData.TAG_GET_FOLLOWERS_COUNT&&
                call.request().tag()==MainData.TAG_GET_FOLLOWING_COUNT&&
                call.request().tag()==MainData.TAG_FOLLOW&&
                call.request().tag()==MainData.TAG_VIEW_ACC){
                call.cancel()
            }
        }
        for(call in client.dispatcher.runningCalls()){
            if(call.request().tag()==MainData.TAG_GET_FOLLOWERS_COUNT&&
                call.request().tag()==MainData.TAG_GET_FOLLOWING_COUNT&&
                call.request().tag()==MainData.TAG_FOLLOW&&
                call.request().tag()==MainData.TAG_VIEW_ACC){
                call.cancel()
            }
        }
    }
    fun getFollowersCount(userId: Long) {
        var cookies: MutableList<HttpCookie>
        val urlHttp = if (userId == 0.toLong()) {
            HttpUrl.Builder().scheme("https")
                .host(MainData.BASE_URL)
                .addPathSegment(MainData.URL_PREFIX_USER_PROFILE)
                .addPathSegment("followers")
                .build()
        } else {
            HttpUrl.Builder().scheme("https")
                .host(MainData.BASE_URL)
                .addPathSegment(MainData.URL_PREFIX_USER_PROFILE)
                .addPathSegment("followers")
                .addQueryParameter("id", userId.toString())
                .build()
        }
        val request = Request.Builder().url(urlHttp)
            .tag(MainData.TAG_GET_FOLLOWERS_COUNT)
            .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
            .addHeader("Cookie", "csrftoken=$csrftoken; sessionid=$sessionId")
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val count = response.body!!.string()
                    if (fragment is AccountFragment) {
                        (fragment.context as AppCompatActivity).runOnUiThread {
                            fragment.followers_count.text = count
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

    fun getFollowingCount(userId: Long) {
        var cookies: MutableList<HttpCookie>
        val urlHttp = if (userId == 0.toLong()) {
            HttpUrl.Builder().scheme("https")
                .host(MainData.BASE_URL)
                .addPathSegment(MainData.URL_PREFIX_USER_PROFILE)
                .addPathSegment("check_i_follow")
                .build()
        } else {
            HttpUrl.Builder().scheme("https")
                .host(MainData.BASE_URL)
                .addPathSegment(MainData.URL_PREFIX_USER_PROFILE)
                .addPathSegment("check_i_follow")
                .addQueryParameter("id", userId.toString())
                .build()
        }
        val request = Request.Builder().url(urlHttp)
            .tag(MainData.TAG_GET_FOLLOWING_COUNT)
            .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
            .addHeader("Cookie", "csrftoken=$csrftoken; sessionid=$sessionId")

            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val count = response.body!!.string()
                    if (fragment is AccountFragment) {
                        (fragment.context as AppCompatActivity).runOnUiThread {
                            fragment.followed_count.text = count
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

    fun follow(userId: Long?) {
        if (userId != null) {
            var cookies: MutableList<HttpCookie>
            val urlHttp = HttpUrl.Builder().scheme("https")
                .host(MainData.BASE_URL)
                .addPathSegment(MainData.URL_PREFIX_USER_PROFILE)
                .addPathSegment(MainData.FOLLOW_API)
                .addQueryParameter("id", userId.toString())
                .build()
            val request = Request.Builder().url(urlHttp)
                .tag(MainData.TAG_FOLLOW)
                .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
                .addHeader("Cookie", "csrftoken=$csrftoken; sessionid=$sessionId")
                .build()
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val isLogin = response.header("isLogin", "true")
                        if (fragment is AccountFragment) {
                            (fragment.context as AppCompatActivity).runOnUiThread {
                                fragment.followed()
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
    }

    fun viewAcc(userId: Long) {
        val formBody = FormBody.Builder()
            .add("id", userId.toString())
            .add("csrfmiddlewaretoken", csrftoken)
            .build()
        val request = Request.Builder()
            .tag(MainData.TAG_VIEW_ACC)
            .url("https://${MainData.BASE_URL}/${MainData.URL_PREFIX_USER_PROFILE}/${MainData.VIEW_ACC}")
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
                    val body = response.body?.string()
                    if (body != null) {
                        val jsonObject = JSONObject(body)
                        val keys = jsonObject.keys()
                        val idOfUser = jsonObject["isMyUser"]
                        val postsList = jsonObject["posts"]
                        val listOfPosts= mutableListOf<Post>()
                        if (postsList is JSONArray) {
                            val length = postsList.length()
                            for(i in 0 until length){
                                val value=postsList[i]
                                if(value is JSONObject)
                                listOfPosts.add(Post(
                                    id=value["post_id"].toString().toLong(),
                                    imageUrl = value["src_mini"].toString(),
                                    date = value["date"].toString().toLong(),
                                    postText = value["description"].toString()
                                ))
                            }
                        }
                        if(fragment is AccountPostTableRecyclerFragment){
                            (fragment.context as AppCompatActivity).runOnUiThread{
                                fragment.addPhotos(listOfPosts)
                            }
                        }
                    }
                    val isI = response.header("isI")
                    if (fragment is AccountFragment) {
                        (fragment.context as AppCompatActivity).runOnUiThread {
                            fragment.notI()
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
}