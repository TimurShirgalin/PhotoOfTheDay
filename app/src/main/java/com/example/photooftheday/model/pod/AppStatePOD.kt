package com.example.photooftheday.model.pod

sealed class AppStatePOD {
    data class Success(val serverResponseData: DataPOD) : AppStatePOD()
    data class Error(val error: Throwable) : AppStatePOD()
    data class Loading(val progress: Int?) : AppStatePOD()
}