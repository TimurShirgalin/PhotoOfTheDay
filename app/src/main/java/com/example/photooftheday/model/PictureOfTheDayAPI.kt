package com.example.photooftheday.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PictureOfTheDayAPI {
    @GET("planetary/apod")
    fun getPOD(
        @Query("api_key") api: String,
        @Query("date") date: String
    ): Call<DataPOD>
}