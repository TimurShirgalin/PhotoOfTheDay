package com.example.photooftheday.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataPOD(
    val date: String?,
    val explanation: String?,
    val mediaType: String?,
    val title: String?,
    val url: String?,
) : Parcelable