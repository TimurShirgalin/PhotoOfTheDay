package com.example.photooftheday.model.mars

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DataMarsAPI {
    @GET("mars-photos/api/v1/rovers/curiosity/photos")
    fun getMarsData(
        @Query("api_key") api: String,
        @Query("camera") camera: String,
        @Query("sol") sol: Int,
        @Query("page") page: Int
    ): Call<DataMars>
}