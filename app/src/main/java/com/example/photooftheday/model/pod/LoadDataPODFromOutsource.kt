package com.example.photooftheday.model.pod

import androidx.lifecycle.MutableLiveData
import com.example.photooftheday.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


const val NASA_API_KEY = BuildConfig.NASA_APIKEY

class LoadDataPODFromOutsource {

    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()

    fun sendServerRequest(date: String, liveDataForViewToObserve: MutableLiveData<AppStatePOD>) {
        val apiKey: String = NASA_API_KEY
        if (apiKey.isBlank()) {
            AppStatePOD.Error(Throwable("You need API key"))
        } else {
            retrofitImpl.getRetrofitImpl().getPOD(apiKey, date).enqueue(object : Callback<DataPOD> {
                override fun onResponse(call: Call<DataPOD>, response: Response<DataPOD>) {
                    if (response.isSuccessful && response.body() != null) {
                        liveDataForViewToObserve.value = AppStatePOD.Success(response.body()!!)
                    } else {
                        val message = response.message()
                        if (message.isNullOrEmpty()) {
                            liveDataForViewToObserve.value =
                                AppStatePOD.Error(Throwable("Unidentified error"))
                        } else {
                            liveDataForViewToObserve.value = AppStatePOD.Error(Throwable(message))
                        }
                    }
                }

                override fun onFailure(call: Call<DataPOD>, t: Throwable) {
                    liveDataForViewToObserve.value = AppStatePOD.Error(t)
                }
            })
        }
    }
}