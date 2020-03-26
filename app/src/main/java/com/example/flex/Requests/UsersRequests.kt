package com.example.flex.Requests

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flex.Fragments.AccountFragment
import com.example.flex.Fragments.TvFragment
import com.example.flex.MainData
import com.example.flex.POJO.User
import kotlinx.android.synthetic.main.fragment_account.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpCookie

class UsersRequests(private val activity: Fragment) {
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

    fun getFollowersCount(userId: Long) {
        var cookies: MutableList<HttpCookie>
        val urlHttp = if (userId == 0.toLong()) {
            HttpUrl.Builder().scheme("https")
                .host(MainData().BASE_URL)
                .addPathSegment(MainData().URL_PREFIX_USER_PROFILE)
                .addPathSegment("followers")
                .build()
        } else {
            HttpUrl.Builder().scheme("https")
                .host(MainData().BASE_URL)
                .addPathSegment(MainData().URL_PREFIX_USER_PROFILE)
                .addPathSegment("followers")
                .addQueryParameter("id", userId.toString())
                .build()
        }
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
                    val count = response.body!!.string()
                    if (activity is AccountFragment) {
                        (activity.context as AppCompatActivity).runOnUiThread {
                            activity.followers_count.text = count
                        }
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
                .host(MainData().BASE_URL)
                .addPathSegment(MainData().URL_PREFIX_USER_PROFILE)
                .addPathSegment("check_i_follow")
                .build()
        } else {
            HttpUrl.Builder().scheme("https")
                .host(MainData().BASE_URL)
                .addPathSegment(MainData().URL_PREFIX_USER_PROFILE)
                .addPathSegment("check_i_follow")
                .addQueryParameter("id", userId.toString())
                .build()
        }
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
                    val count = response.body!!.string()
                    if (activity is AccountFragment) {
                        (activity.context as AppCompatActivity).runOnUiThread {
                            activity.followed_count.text = count
                        }
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
                .host(MainData().BASE_URL)
                .addPathSegment(MainData().URL_PREFIX_USER_PROFILE)
                .addPathSegment(MainData().FOLLOW_API)
                .addQueryParameter("id", userId.toString())
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
                        val isLogin = response.header("isLogin", "true")
                        if (activity is AccountFragment) {
                            (activity.context as AppCompatActivity).runOnUiThread {
                                activity.followed()
                            }
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
            .build()
        val request = Request.Builder()
            .url("https://${MainData().BASE_URL}/${MainData().URL_PREFIX_USER_PROFILE}/${MainData().VIEW_ACC}")
            .post(formBody)
            .addHeader(MainData().HEADER_REFRER, "https://" + MainData().BASE_URL)
            .addHeader("Cookie", "csrftoken=$csrftoken; sessionid=$sessionId")
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val isI = response.header("isI")
                    if (isI == "true") {
                        if (activity is AccountFragment) {
                            (activity.context as AppCompatActivity).runOnUiThread {
                                activity.notI()
                            }
                        }
                    } else if (isI == "false") {
                        if (activity is AccountFragment) {
                            (activity.context as AppCompatActivity).runOnUiThread {
                                activity.yesI()
                            }
                        }
                    }
                } else {

                }
            }
        })
    }
}