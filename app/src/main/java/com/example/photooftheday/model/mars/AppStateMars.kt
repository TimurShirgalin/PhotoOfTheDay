package com.example.photooftheday.model.mars

sealed class AppStateMars {
    data class Success(val serverResponseDataMars: MutableList<MarsPhoto>) : AppStateMars()
    data class Error(val error: Throwable) : AppStateMars()
    data class Loading(val progress: Int?) : AppStateMars()
}