package com.john.audioplayer.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.media.audiofx.Visualizer
import javax.inject.Inject

class AudioPlayerManager @Inject constructor(val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var equalizer: Equalizer? = null
    private var visualizer: Visualizer? = null

    private var onCompletionCallback: (() -> Unit)? = null

    fun setOnCompletionListener(callback: () -> Unit) {
        onCompletionCallback = callback
    }


    fun load(fileName: String) {
        release()
        val afd = context.assets.openFd(fileName)
        mediaPlayer = MediaPlayer().apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            setOnPreparedListener {
                start()
                setupEqualizer()
                setupVisualizer()
            }
            setOnCompletionListener {
                onCompletionCallback?.invoke()
            }
            prepareAsync()
        }
        setupEqualizer()
        setupVisualizer()
    }

    private fun setupEqualizer() {
        mediaPlayer?.let {
            equalizer = Equalizer(0, it.audioSessionId).apply {
                enabled = true
            }
        }
    }

    private fun setupVisualizer() {
        mediaPlayer?.let { mp ->
            try {
                if (mp.audioSessionId != 0) {
                    visualizer = Visualizer(mp.audioSessionId).apply {
                        captureSize = Visualizer.getCaptureSizeRange()[1]
                        enabled = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun play() = mediaPlayer?.start()
    fun pause() = mediaPlayer?.pause()
    fun isPlaying() = mediaPlayer?.isPlaying ?: false
    fun duration() = mediaPlayer?.duration ?: 0
    fun currentPosition() = mediaPlayer?.currentPosition ?: 0
    fun seekTo(pos: Int) = mediaPlayer?.seekTo(pos)

    fun getWaveform(): ByteArray {
        val bytes = ByteArray(visualizer?.captureSize ?: 0)
        visualizer?.getWaveForm(bytes)
        return bytes
    }

    fun setBandLevel(band: Short, level: Short) {
        equalizer?.setBandLevel(band, level)
    }

    fun getBandCount() = equalizer?.numberOfBands ?: 0
    fun getBandRange() = equalizer?.bandLevelRange ?: shortArrayOf(0, 0)


    fun release() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        equalizer?.release()
        equalizer = null

        visualizer?.release()
        visualizer = null
    }
}