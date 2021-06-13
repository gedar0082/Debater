package com.gedar0082.debater.model.net.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Class for access to the server's REST api. Access from another classes and packages is
 * through the variable service
 */
object ApiFactory {

    val service : ApiService = getBuilder().create(ApiService::class.java)

    private fun getBuilder(): Retrofit = Retrofit.Builder()
        .client(buildClient())
//        .baseUrl("http://192.168.1.249:8080/api/")
//        .baseUrl("http://134.0.118.137:8080/api/")
        .baseUrl("http://debaterbachelor.xyz:8080/api/")
        .addConverterFactory(JacksonConverterFactory.create())
        .build()

    private fun buildClient() = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()
}