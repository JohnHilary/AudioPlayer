package com.john.audioplayer.state

import android.graphics.Bitmap

data class AudioPlayerScreenUiState(
    val progress: Int = 0,
    val duration: Int = 0,
    val isPlaying: Boolean = true,
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val albumArt: Bitmap? = null,
    val waveForm: List<Byte> = emptyList(),
    val bandLevels: List<Float> = emptyList()
)