package com.example.photooftheday.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photooftheday.model.recycler.Change
import com.example.photooftheday.model.recycler.DataRecycler
import com.example.photooftheday.model.recycler.createCombinedPayload
import com.example.photooftheday.model.recycler.getPhotoPODData

class ViewModelRecycler(
    private val liveDataForViewToObserve: MutableLiveData<List<DataRecycler>> = MutableLiveData(),
) : ViewModel() {

    fun getData(): LiveData<List<DataRecycler>> {
        liveDataForViewToObserve.value = getPhotoPODData()
        return liveDataForViewToObserve
    }
}