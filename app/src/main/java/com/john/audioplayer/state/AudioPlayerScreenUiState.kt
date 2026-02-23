package com.john.audioplayer.state

import com.john.audioplayer.model.AudioInfo

data class AudioPlayerScreenUiState(
    val audioInfo: AudioInfo? = null,
    val progress: Int = 0,
    val isPlaying: Boolean = true,
    val waveForm: List<Byte> = emptyList(),
    val bandLevels: List<Float> = emptyList()
)