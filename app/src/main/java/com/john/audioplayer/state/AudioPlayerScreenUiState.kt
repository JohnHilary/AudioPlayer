package com.john.audioplayer.state

data class AudioPlayerScreenUiState(
    val progress: Int = 0,
    val duration: Int = 0,
    val isPlaying: Boolean = true,
    val waveForm: List<Byte> = emptyList(),
    val bandLevels: List<Float> = emptyList()
)