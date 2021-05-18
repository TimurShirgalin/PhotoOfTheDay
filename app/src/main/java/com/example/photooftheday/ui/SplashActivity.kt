package com.example.photooftheday.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.photooftheday.R
import com.example.photooftheday.ui.main.MainActivity
import com.google.android.material.imageview.ShapeableImageView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ShapeableImageView>(R.id.image_splash)
        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.splash_animation)
        logo.animation = animation
        logo.animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                overridePendingTransition(0, 0)
                finish()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
    }
}