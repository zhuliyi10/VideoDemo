package com.leory.videodemo.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.io.IOException;

public class VideoTextureView extends FrameLayout implements TextureView.SurfaceTextureListener {
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private Context mContext;
    private MediaPlayer mMediaPlayer = null;
    private TextureView mTextureView;
    private Surface mSurface;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private int mCurrentState = STATE_IDLE;
    private Uri mVideoUri;
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;

    public VideoTextureView(Context context) {
        this(context, null);
    }

    public VideoTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mTextureView = new TextureView(mContext);
        addView(mTextureView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mTextureView.setSurfaceTextureListener(this);
        setBackgroundColor(Color.BLACK);
    }


    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        mVideoUri = uri;
        openVideo();
    }

    private void openVideo() {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnPreparedListener(mPreparedListener);
                mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
                mMediaPlayer.setOnCompletionListener(mCompletionListener);
//            mMediaPlayer.setOnErrorListener(mErrorListener);
//            mMediaPlayer.setOnInfoListener(mInfoListener);
//            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
                mMediaPlayer.setDataSource(mContext, mVideoUri);
                mMediaPlayer.setSurface(mSurface);
                mMediaPlayer.setLooping(false);
                mMediaPlayer.setScreenOnWhilePlaying(true);
            } else {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mContext, mVideoUri);
            }
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
        } catch (IOException ex) {
            mCurrentState = STATE_ERROR;
            return;
        } catch (IllegalArgumentException ex) {
            mCurrentState = STATE_ERROR;
            return;
        }
    }

    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
    }

    public void resume() {
        if (isInPlaybackState()) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                mCurrentState = STATE_PLAYING;
            }
        }
    }

    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
    }


    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
        }
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
        }
    };
    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            mCurrentState = STATE_PREPARED;
            start();
        }
    };
    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = (mp, width, height) -> {
        mVideoWidth = mp.getVideoWidth();
        mVideoHeight = mp.getVideoHeight();
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            int viewWidth = getMeasuredWidth();
            int viewHeight = viewWidth * mVideoHeight / mVideoWidth;
            LayoutParams lp = (LayoutParams) mTextureView.getLayoutParams();
            lp.height = viewHeight;
            lp.width = viewWidth;
            mTextureView.setLayoutParams(lp);
        }
    };

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        if (mMediaPlayer != null) {
            mMediaPlayer.setSurface(mSurface);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }
}
