package com.example.photooftheday.model.mars

import androidx.lifecycle.MutableLiveData
import com.example.photooftheday.model.pod.NASA_API_KEY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadDataMarsFromOutsource {

    private val marsRetrofitImpl: MarsRetrofitImpl = MarsRetrofitImpl()

    fun sendMarsServerRequest(
        liveDataForViewToObserve: MutableLiveData<AppStateMars>
    ) {
        val camera = listOf("NAVCAM", "FHAZ")
        val cameraPhoto = mutableListOf<MarsPhoto>()
        val apiKey: String = NASA_API_KEY
        if (apiKey.isBlank()) {
            AppStateMars.Error(Throwable("You need API key"))
        } else {
            for (cam in camera) {
                marsRetrofitImpl.getMarsRetrofitImpl().getMarsData(apiKey, cam, 2000, 1)
                    .enqueue(object : Callback<DataMars> {
                        override fun onResponse(
                            call: Call<DataMars>, response: Response<DataMars>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                cameraPhoto.add(MarsPhoto(cam, response.body()!!.photos))
                                liveDataForViewToObserve.value = AppStateMars.Success(cameraPhoto)
                            } else {
                                val message = response.message()
                                if (message.isNullOrEmpty()) {
                                    liveDataForViewToObserve.value =
                                        AppStateMars.Error(Throwable("Unidentified error"))
                                } else {
                                    liveDataForViewToObserve.value =
                                        AppStateMars.Error(Throwable(message))
                                }
                            }
                        }

                        override fun onFailure(call: Call<DataMars>, t: Throwable) {
                            liveDataForViewToObserve.value = AppStateMars.Error(t)
                        }
                    })
            }
        }
    }
}