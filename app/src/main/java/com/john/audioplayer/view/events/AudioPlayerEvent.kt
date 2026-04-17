package com.john.audioplayer.view.events

import com.john.audioplayer.audio.FileType

sealed interface AudioPlayerEvent {

    data class LoadSong(val fileType: FileType) : AudioPlayerEvent

    data class SeekTo(val value: Float) : AudioPlayerEvent

    data class ChangeBand(val index: Int, val value: Float) : AudioPlayerEvent

    object StartProgressUpdater : AudioPlayerEvent

    data object PlayPause : AudioPlayerEvent

    data object PlayNext : AudioPlayerEvent
    data object PlayPrevious : AudioPlayerEvent

    data object SetupVisualizer : AudioPlayerEvent

}