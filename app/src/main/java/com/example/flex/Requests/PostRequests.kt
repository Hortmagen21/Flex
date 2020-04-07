package com.example.flex.Requests

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flex.Fragments.AccountPostListRecyclerFragment
import com.example.flex.Fragments.AccountPostTableRecyclerFragment
import com.example.flex.Fragments.CameraFragment
import com.example.flex.Fragments.HomeFragment
import com.example.flex.MainData
import com.example.flex.POJO.Post
import com.example.flex.SignIn
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy

class PostRequests (
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
            if(call.request().tag()==MainData.TAG_VIEW_ALL_POSTS_HOME&&
                call.request().tag()==MainData.TAG_VIEW_ALL_POSTS_ACCOUNT&&
                call.request().tag()==MainData.TAG_COMMENT&&
                call.request().tag()==MainData.TAG_LIKE){
                call.cancel()
            }
        }
        for(call in client.dispatcher.runningCalls()){
            if(call.request().tag()==MainData.TAG_VIEW_ALL_POSTS_HOME&&
                call.request().tag()==MainData.TAG_VIEW_ALL_POSTS_ACCOUNT&&
                call.request().tag()==MainData.TAG_COMMENT&&
                call.request().tag()==MainData.TAG_LIKE){
                call.cancel()
            }
        }
    }
    fun  likePost(postId:Long){
        val formBody = FormBody.Builder()
            .add("id", postId.toString())
            .add("csrfmiddlewaretoken", csrftoken)
            .build()
        val request = Request.Builder()
            .tag(MainData.TAG_LIKE)
            .url("https://${MainData.BASE_URL}/${MainData.URL_PREFIX_USER_PROFILE}/${MainData.LIKE}")
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

                }else  if(response.code== MainData.ERR_403){
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
    fun  commentPost(postId:Long){
        val formBody = FormBody.Builder()
            .add("id", postId.toString())
            .add("comment",".")
            .add("csrfmiddlewaretoken", csrftoken)
            .build()
        val request = Request.Builder()
            .tag(MainData.TAG_COMMENT)
            .url("https://${MainData.BASE_URL}/${MainData.URL_PREFIX_USER_PROFILE}/${MainData.COMMENT}")
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

                }else  if(response.code== MainData.ERR_403){
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
    fun viewAllPostsAccount(id:Long){
        val formBody = FormBody.Builder()
            .add("id", id.toString())
            .add("csrfmiddlewaretoken", csrftoken)
            .build()
        val request = Request.Builder()
            .tag(MainData.TAG_VIEW_ALL_POSTS_ACCOUNT)
            .url("https://${MainData.BASE_URL}/${MainData.URL_PREFIX_USER_PROFILE}/${MainData.VIEW_ALL_POSTS}")
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
                                    listOfPosts.add(
                                        Post(
                                            id=value["post_id"].toString().toLong(),
                                            imageUrl = value["src"].toString(),
                                            date = value["date"].toString().toLong(),
                                            postText = value["description"].toString(),
                                            countOfFires = value["likes"].toString().toLong(),
                                            countOfComments = value["comments"].toString().toLong()
                                        )
                                    )
                            }
                        }
                        if(fragment is AccountPostListRecyclerFragment){
                            (fragment.context as AppCompatActivity).runOnUiThread{
                                fragment.addPosts(listOfPosts)
                            }
                        }
                    }
                }else  if(response.code== MainData.ERR_403){
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
    fun viewAllPostsHome(lastId:Long){
        val formBody = HttpUrl.Builder().scheme("https")
            .host(MainData.BASE_URL)
            .addPathSegment(MainData.URL_PREFIX_HOME)
            .addPathSegment(MainData.HOME)
            .addQueryParameter("id", lastId.toString())
            .build()
        val request = Request.Builder()
            .tag(MainData.TAG_VIEW_ALL_POSTS_HOME)
            .url(formBody)
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
                        val postsList = jsonObject["posts"]
                        val listOfPosts= mutableListOf<Post>()
                        if (postsList is JSONArray) {
                            val length = postsList.length()
                            for(i in 0 until length){
                                val value=postsList[i]
                                if(value is JSONObject)
                                    listOfPosts.add(
                                        Post(
                                            id=value["id"].toString().toLong(),
                                            imageUrl = value["src"].toString(),
                                            date = value["date"].toString().toLong(),
                                            postText = value["description"].toString(),
                                            countOfFires = value["likes"].toString().toLong(),
                                            countOfComments = value["comments"].toString().toLong()
                                        )
                                    )
                            }
                        }
                        if(fragment is HomeFragment){
                            (fragment.context as AppCompatActivity).runOnUiThread{
                                fragment.addPosts(listOfPosts)
                            }
                        }
                    }
                }else  if(response.code== MainData.ERR_403){
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