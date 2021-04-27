package com.example.photooftheday.ui.secondary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.photooftheday.R

class ApiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api)
        findViewById<ViewPager2>(R.id.view_pager).adapter = ViewPagerAdapter()
    }
}