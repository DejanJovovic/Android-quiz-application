package com.deksi.graduationquiz.mediaPlayer

import android.content.Context
import android.media.MediaPlayer

class MediaPlayerManager {
    companion object {
        private var mediaPlayer: MediaPlayer? = null

        fun initMediaPlayer(context: Context, resourceId: Int) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context.applicationContext, resourceId)
                mediaPlayer?.isLooping = true
            }
        }

        fun start() {
            mediaPlayer?.start()
        }

        fun pause() {
            mediaPlayer?.pause()
        }

        fun release() {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}