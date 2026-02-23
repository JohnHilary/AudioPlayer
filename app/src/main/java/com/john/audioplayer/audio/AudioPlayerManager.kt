package com.john.audioplayer.audio

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.media.audiofx.Visualizer
import android.util.Log
import com.john.audioplayer.model.AudioInfo
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
        mediaPlayer?.let { player ->
            try {
                equalizer = Equalizer(0, player.audioSessionId).apply { enabled = true }
            } catch (e: Exception) {
                Log.e("AudioPlayer", "Cannot initialize equalizer", e)
            }
        }
    }
    private fun setupVisualizer() {
        mediaPlayer?.let { mp ->
            try {
                if (mp.audioSessionId != 0) {
                    visualizer = Visualizer(mp.audioSessionId).apply {
                        enabled = false
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

    fun getAudioInfo(fileName: String): AudioInfo {
        val afd = context.assets.openFd(fileName)
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)

        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: 0
        val albumArt = retriever.embeddedPicture?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        return AudioInfo(title, artist, album,  duration,albumArt)
    }

}