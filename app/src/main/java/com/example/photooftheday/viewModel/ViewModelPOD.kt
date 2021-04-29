package com.example.photooftheday.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photooftheday.model.pod.AppStatePOD
import com.example.photooftheday.model.pod.LoadDataPODFromOutsource
import java.text.SimpleDateFormat
import java.util.*

class ViewModelPOD(
    private val liveDataForViewToObserve: MutableLiveData<AppStatePOD> = MutableLiveData()
) : ViewModel() {

    @SuppressLint("SimpleDateFormat")
    fun getData(previousDays: Int): LiveData<AppStatePOD> {
        val date: String = let {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -previousDays)
            SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        }

        liveDataForViewToObserve.value = AppStatePOD.Loading(null)
        LoadDataPODFromOutsource().sendServerRequest(date, liveDataForViewToObserve)
        return liveDataForViewToObserve
    }
}