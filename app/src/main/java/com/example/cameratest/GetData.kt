package com.example.cameratest
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private const val BASE_URL =
    "https://nuagenet3.herokuapp.com/"


/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()
/**
 * A public interface that exposes the [getProperties] method
 */
interface WebServerAPI {
    /**
     * Returns a Coroutine [Deferred] [List] of [MarsProperty] which can be fetched with await() if
     * in a Coroutine scope.
     * The @GET annotation indicates that the "realestate" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("/")
    fun getProperties():
    // The Coroutine Call Adapter allows us to return a Deferred, a Job with a result
            Deferred<String>
    @Multipart
    @POST("/analyze")
        fun uploadFile(@Part file: MultipartBody.Part?): Deferred<String>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object API_obj {
    val retrofitService : WebServerAPI by lazy { retrofit.create(WebServerAPI::class.java) }
}