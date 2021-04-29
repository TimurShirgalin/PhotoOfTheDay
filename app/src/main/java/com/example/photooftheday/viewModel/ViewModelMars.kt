package com.example.photooftheday.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photooftheday.model.mars.AppStateMars
import com.example.photooftheday.model.mars.LoadDataMarsFromOutsource

class ViewModelMars(
    private val liveDataForMarsViewToObserve: MutableLiveData<AppStateMars> = MutableLiveData()
) : ViewModel() {

    fun getDataMars(): LiveData<AppStateMars> {
        liveDataForMarsViewToObserve.value = AppStateMars.Loading(null)
        LoadDataMarsFromOutsource().sendMarsServerRequest(liveDataForMarsViewToObserve)
        return liveDataForMarsViewToObserve
    }
}