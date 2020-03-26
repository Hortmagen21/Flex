package com.example.flex.Requests

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flex.Adapter.SearchAdapter
import com.example.flex.Fragments.TvFragment
import com.example.flex.MainData
import com.example.flex.POJO.User
import com.example.flex.SignIn
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpCookie

class SearchRequests(private val activity: Fragment) {
    private val client: OkHttpClient
    private val cookieManager = CookieManager()
    var sessionId = ""
    var csrftoken = ""

    init {
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()
    }

    fun search(search: String, searchAdapter: SearchAdapter) {
        var cookies: MutableList<HttpCookie>
        val urlHttp = HttpUrl.Builder().scheme("https")
            .host(MainData().BASE_URL)
            .addPathSegment(MainData().URL_PREFIX_TV_SHOWS)
            .addPathSegment("search_people")
            .addQueryParameter("name", search)
            .build()
        val request = Request.Builder().url(urlHttp)
            .addHeader(MainData().HEADER_REFRER, "https://" + MainData().BASE_URL)
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
                        val post1: JSONObject
                        var map = mutableMapOf<String, Long>()
                        if (post != null) {
                            post1 = JSONObject(post)
                            map = toMap(post1)
                        }
                        val users = mutableListOf<User>()
                        for (user in map) {
                            users.add(User(user.value, user.key))
                        }
                        if (activity is TvFragment)
                            (activity.context as AppCompatActivity).runOnUiThread {
                                activity.searchAdapter.setUsers(users)
                            }
                    } else {
                        (activity.context as AppCompatActivity).runOnUiThread {
                            val intent = Intent(activity.context, SignIn().javaClass)
                            (activity.context as AppCompatActivity).startActivity(intent)
                        }
                    }
                    cookies = cookieManager.cookieStore.cookies
                } else {

                }
            }
        })
    }

    fun toMap(json: JSONObject): MutableMap<String, Long> {
        var map = mutableMapOf<String, Long>()
        var keys = json.keys()
        while (keys.hasNext()) {
            var key = keys.next()
            var value = json.get(key)
            if (value is JSONObject) {
                value = toMap(value)
                map.putAll(value)
            } else if (value is JSONArray) {
                value = toList(value)
            } else {
                map[key] = value.toString().toLong()
            }
        }
        return map
    }

    fun toList(json: JSONArray): List<Long> {
        var list = mutableListOf<Long>()
        for (i in 0..json.length()) {
            var value = json.get(i)
            if (value is JSONObject) {
                value = toMap(value)
            } else if (value is JSONArray) {
                value = toList(value)
                list.addAll(value)
            } else {
                list.add(value.toString().toLong())
            }

        }
        return list
    }
}