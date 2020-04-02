package com.arnaudcayrol.WhatIsThatCloud.network
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private const val BASE_URL =
    "https://nuagenet3.herokuapp.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()


interface WebServerAPI {
    /**
     * Returns a Coroutine [Deferred] [CloudList] which can be fetched with await() if
     * in a Coroutine scope.
     */

//    @GET("/")
//    fun getProperties():
//    // The Coroutine Call Adapter allows us to return a Deferred, a Job with a result
//            Deferred<String>

    @Multipart
    @POST("/analyze")
        fun uploadFile(@Part file: MultipartBody.Part?): Deferred<CloudList>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object API_obj {
    val retrofitService : WebServerAPI by lazy {
        retrofit.create(WebServerAPI::class.java)
    }
}