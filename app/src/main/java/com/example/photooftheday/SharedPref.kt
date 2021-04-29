package com.example.photooftheday

import android.content.Context
import android.content.SharedPreferences


const val THEME = "NIGHT_MODE"
const val DAY = "DAY"
const val HD = "HD"

class SharedPref(context: Context) {
    private val SHARED_PREF = "SHARED_PREF"
    private var sP: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)

    fun setSharedPref(id: String, value: Int) = sP.edit().putInt(id, value).apply()


    fun getSharedPref(id: String): Int = sP.getInt(id, 0)
}