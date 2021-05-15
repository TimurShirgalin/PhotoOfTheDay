package com.example.photooftheday.model.mars

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataMars(val photos: List<DataMarsPhoto>) : Parcelable

@Parcelize
data class DataMarsPhoto(val img_src: String) : Parcelable

data class MarsPhoto(val camera: String, val urls: List<DataMarsPhoto>)