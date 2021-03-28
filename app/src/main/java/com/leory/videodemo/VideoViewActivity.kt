package com.leory.videodemo

import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_vdieo_view.*

class VideoViewActivity : AppCompatActivity() {

    private var mCurTime = 0
    private var mDownY = 0f;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_vdieo_view)
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.video1)

        video_view.setVideoURI(uri)
        video_view.start()
        video_view.setOnCompletionListener {
            video_view.setVideoURI(uri)
            video_view.start()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    mDownY = it.y
                }
                MotionEvent.ACTION_MOVE -> {
                    var dy = it.y - mDownY;
                    video_view.translationY = dy
                }
                MotionEvent.ACTION_UP -> {
                    backAnim()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onResume() {
        video_view.resume()
        video_view.seekTo(mCurTime)
        super.onResume()
    }

    override fun onPause() {
        video_view.pause()
        mCurTime = video_view.currentPosition
        super.onPause()
    }

    override fun onDestroy() {
        video_view.stopPlayback()
        super.onDestroy()
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun backAnim() {
        var tranY = video_view.translationY
        val animator = ObjectAnimator.ofFloat(video_view, "translationY", tranY, 0f)
        animator.duration = 200
        animator.interpolator = LinearInterpolator()
        animator.start()
    }
}