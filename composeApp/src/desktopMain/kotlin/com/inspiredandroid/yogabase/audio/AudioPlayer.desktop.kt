package com.inspiredandroid.yogabase.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

@Composable
actual fun rememberAudioPlayer(): AudioPlayer {
    val player = remember { DesktopAudioPlayer() }
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }
    return player
}

class DesktopAudioPlayer : AudioPlayer {
    private var clip: Clip? = null

    override fun play(data: ByteArray, loop: Boolean) {
        stop()
        try {
            // `javax.sound.sampled` cannot natively decode MP3; the mp3spi JAR on the
            // classpath registers an SPI that returns an MP3-encoded stream, which we
            // then convert to PCM before opening the clip. Works for WAV too.
            val raw: AudioInputStream = AudioSystem.getAudioInputStream(ByteArrayInputStream(data))
            val baseFormat = raw.format
            val decodedFormat = AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.sampleRate,
                16,
                baseFormat.channels,
                baseFormat.channels * 2,
                baseFormat.sampleRate,
                false,
            )
            val decoded = AudioSystem.getAudioInputStream(decodedFormat, raw)
            clip = AudioSystem.getClip().apply {
                open(decoded)
                if (loop) loop(Clip.LOOP_CONTINUOUSLY)
                start()
            }
        } catch (_: Exception) {
        }
    }

    override fun stop() {
        clip?.stop()
        clip?.close()
        clip = null
    }

    override fun pause() {
        clip?.stop()
    }

    override fun resume() {
        clip?.start()
    }

    override fun release() = stop()
}
