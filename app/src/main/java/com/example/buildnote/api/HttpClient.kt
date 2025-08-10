package com.example.buildnote.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HttpClient {
    private const val BASE_URL = "http://10.0.2.2:8080/api/"


    val api: AppointmentApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AppointmentApi::class.java)
    }
}