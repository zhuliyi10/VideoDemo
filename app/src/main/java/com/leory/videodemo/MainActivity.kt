package com.leory.videodemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        video_view.setOnClickListener {
            startActivity(Intent(MainActivity@ this, VideoViewActivity::class.java))
        }
        media_player_surface.setOnClickListener {
            startActivity(Intent(MainActivity@ this, SurfaceActivity::class.java))
        }
        media_player_texture.setOnClickListener {
            startActivity(Intent(MainActivity@ this, TextureViewActivity::class.java))
        }
    }
}