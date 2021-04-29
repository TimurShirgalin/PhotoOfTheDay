package com.example.photooftheday.model.pod

import com.example.photooftheday.model.StartRetrofit

const val baseUrl = "https://api.nasa.gov/"

class PODRetrofitImpl {
    fun getRetrofitImpl(): PictureOfTheDayAPI {
        val podRetrofit = StartRetrofit(baseUrl).getRetrofit()
        return podRetrofit.create(PictureOfTheDayAPI::class.java)
    }
}