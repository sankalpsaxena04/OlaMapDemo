package com.appscrip.olamapdemo.remote

import android.webkit.WebStorage.Origin
import androidx.core.os.BuildCompat
import com.itkacher.okprofiler.BuildConfig
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import com.appscrip.olamapdemo.model.response.autocompletesearch.OlaSearchAutoCompleteResponse
import com.appscrip.olamapdemo.util.DataConstants.BASE_URL
import com.appscrip.olamapdemo.util.DataConstants.MAP_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AutoCompleteApi {

    @GET("places/v1/autocomplete")
    suspend fun getAutoCompleteSearchResult(
        @Query("input") search: String,
        @Query("api_key") apiKey: String
    ): OlaSearchAutoCompleteResponse
}


object AutoCompleteRetrofitClient {
    private val builder = OkHttpClient.Builder().addInterceptor(OkHttpProfilerInterceptor())
    private val client = builder.build()
    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(MAP_BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
    val apiService: AutoCompleteApi = retrofitBuilder.create(AutoCompleteApi::class.java)
}