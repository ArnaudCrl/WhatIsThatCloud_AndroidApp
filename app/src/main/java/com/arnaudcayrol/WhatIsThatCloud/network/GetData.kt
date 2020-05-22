package com.arnaudcayrol.WhatIsThatCloud.network
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private const val BASE_URL =
    "https://whatisthatcloud.herokuapp.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()


interface WebServerAPI {

    @Multipart
    @POST("/analyze")
        fun uploadFileAsync(@Part file: MultipartBody.Part?): Deferred<CloudList>


    @Multipart
    @POST("/feedback")
        fun uploadFeedbackAsync(@Part file: MultipartBody.Part?): Deferred<Feedback>

    @POST("/wakeup")
        fun wakeupServer(): Deferred<Void>
}



/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object API_obj {
    val retrofitService : WebServerAPI by lazy {
        retrofit.create(WebServerAPI::class.java)
    }
}