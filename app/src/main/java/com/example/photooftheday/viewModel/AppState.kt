package com.example.photooftheday.viewModel

import com.example.photooftheday.model.DataPOD

sealed class AppState {
    data class Success(val serverResponseData: DataPOD) : AppState()
    data class Error(val error: Throwable) : AppState()
    data class Loading(val progress: Int?) : AppState()
}