package com.gedar0082.debater.model.net.api

import com.gedar0082.debater.util.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

object NotificationObject {
    val service : ApiService = getBuilder().create(ApiService::class.java)

    private fun getBuilder(): Retrofit = Retrofit.Builder()
        .client(buildClient())
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(JacksonConverterFactory.create())
        .build()

    private fun buildClient() = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()
}