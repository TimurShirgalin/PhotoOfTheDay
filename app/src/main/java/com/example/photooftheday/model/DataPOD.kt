package com.example.photooftheday.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataPOD(
    val date: String?,
    val explanation: String?,
    val media_type: String?,
    val title: String?,
    val url: String?,
    val code: String?
) : Parcelable