package com.john.audioplayer.view.events

sealed interface AudioPlayerEvent {

    data class LoadSong(val name: String) : AudioPlayerEvent

    data class SeekTo(val value: Float) : AudioPlayerEvent

    data class ChangeBand(val index: Int, val value: Float) : AudioPlayerEvent

    object StartProgressUpdater : AudioPlayerEvent

    data object PlayPause: AudioPlayerEvent

}