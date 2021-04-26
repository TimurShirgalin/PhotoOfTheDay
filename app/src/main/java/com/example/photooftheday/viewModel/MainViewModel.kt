package com.example.photooftheday.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photooftheday.model.AppState
import com.example.photooftheday.model.LoadDataFromOutsource
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    private val liveDataForViewToObserve: MutableLiveData<AppState> = MutableLiveData()
) :
    ViewModel() {

    @SuppressLint("SimpleDateFormat")
    fun getData(previousDays: Int): LiveData<AppState> {
        val date: String = let {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -previousDays)
            SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        }

        liveDataForViewToObserve.value = AppState.Loading(null)
        LoadDataFromOutsource().sendServerRequest(date, liveDataForViewToObserve)
        return liveDataForViewToObserve
    }
}