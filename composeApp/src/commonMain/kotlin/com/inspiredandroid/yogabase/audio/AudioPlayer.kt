package com.inspiredandroid.yogabase.audio

import androidx.compose.runtime.Composable

interface AudioPlayer {
    fun play(data: ByteArray, loop: Boolean = false)
    fun stop()
    fun pause() {}
    fun resume() {}
    fun release()
}

@Composable
expect fun rememberAudioPlayer(): AudioPlayer
