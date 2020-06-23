package com.example.flex

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class PicassoInterceptor(private val csrftoken:String,private val sessionId:String):Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original=chain.request()
        val requestBuilder=original.newBuilder()
            .header(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
            .header("Cookie", "csrftoken=$csrftoken; sessionid=$sessionId")
            .method(original.method,original.body)
        val cacheControl=CacheControl.Builder()
            .maxAge(3,TimeUnit.HOURS)
            .build()
        return chain.proceed(requestBuilder.build()).newBuilder()
            .removeHeader("Cache-Control")
            .header("Cache-Control",cacheControl.toString())
            .build()
    }
}