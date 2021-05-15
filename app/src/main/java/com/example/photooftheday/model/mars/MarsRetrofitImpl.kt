package com.example.photooftheday.model.mars

import com.example.photooftheday.model.StartRetrofit
import com.example.photooftheday.model.pod.baseUrl

class MarsRetrofitImpl {
    fun getMarsRetrofitImpl(): DataMarsAPI {
        val marsRetrofit = StartRetrofit(baseUrl).getRetrofit()
        return marsRetrofit.create(DataMarsAPI::class.java)
    }
}