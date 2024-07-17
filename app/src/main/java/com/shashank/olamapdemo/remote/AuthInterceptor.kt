package com.appscrip.olamapdemo.remote

import okhttp3.Interceptor
import okhttp3.Response

fun mAuthInterceptor(chain: Interceptor.Chain, authToken: String): Response {
    val originalRequest = chain.request()
    val newRequest = originalRequest.newBuilder()
        .addHeader(
            "Authorization",
            "Bearer $authToken"
        )
        .build()
    return chain.proceed(newRequest)
}