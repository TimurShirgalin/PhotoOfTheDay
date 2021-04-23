package com.example.photooftheday.model

import androidx.lifecycle.MutableLiveData
import com.example.photooftheday.viewModel.AppState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val NASA_API_KEY = "7SJTsiHPT62aHqktdd1Kzz933dmoJMK1EzfLwp3S"

class LoadDataFromOutsource {
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()

    fun sendServerRequest(date: String, liveDataForViewToObserve: MutableLiveData<AppState>) {
        val apiKey: String = NASA_API_KEY
        if (apiKey.isBlank()) {
            AppState.Error(Throwable("You need API key"))
        } else {
            retrofitImpl.getRetrofitImpl().getPOD(apiKey, date).enqueue(object :
                Callback<DataPOD> {
                override fun onResponse(
                    call: Call<DataPOD>,
                    response: Response<DataPOD>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        liveDataForViewToObserve.value =
                            AppState.Success(response.body()!!)
                    } else {
                        val message = response.message()
                        if (message.isNullOrEmpty()) {
                            liveDataForViewToObserve.value =
                                AppState.Error(Throwable("Unidentified error"))
                        } else {
                            liveDataForViewToObserve.value =
                                AppState.Error(Throwable(message))
                        }
                    }
                }

                override fun onFailure(
                    call: Call<DataPOD>,
                    t: Throwable
                ) {
                    liveDataForViewToObserve.value = AppState.Error(t)
                }
            })
        }
    }
}