package com.leory.videodemo

import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_surface.*
import kotlinx.android.synthetic.main.activity_surface.video_view
import kotlinx.android.synthetic.main.activity_texture.*

class SurfaceActivity : AppCompatActivity() {

    private var mCurTime = 0
    private var mDownY = 0f
    private var mCurPos = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surface)
        val uri1 = Uri.parse("android.resource://" + packageName + "/" + R.raw.video1)
        val uri2 = Uri.parse("android.resource://" + packageName + "/" + R.raw.video2)
        val uriList = mutableListOf(uri2, uri1)
        video_view.setVideoURI(uriList[mCurPos])

        video_view.setOnCompletionListener {
            mCurPos = if (mCurPos == 0) 1 else 0
            video_view.setVideoURI(uriList[mCurPos])
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
        super.onResume()
    }

    override fun onPause() {
        video_view.pause()
        super.onPause()
    }

    override fun onDestroy() {
        video_view.release()
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