package com.john.audioplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.john.audioplayer.audio.AudioPlayerManager
import com.john.audioplayer.state.AudioPlayerScreenUiState
import com.john.audioplayer.view.events.AudioPlayerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val audioPlayerManager: AudioPlayerManager,
    private val _uiState: MutableStateFlow<AudioPlayerScreenUiState>
) : ViewModel() {

    val uiState = _uiState.asStateFlow()
    private var currentIndex = 0
    private val playlist: List<String> = listOf("kgf.mp3", "audio.mp3", "music.mp3")

    init {
        loadSong(name = playlist[currentIndex])
        startProgressUpdater()
    }

    private fun playNext() {
        currentIndex = (currentIndex + 1) % playlist.size
        val nextSong = playlist[currentIndex]
        loadSong(nextSong)
        audioPlayerManager.play()
        _uiState.update { it.copy(isPlaying = true, progress = 0) }
    }

    private fun playPrevious() {
        currentIndex = if (currentIndex - 1 < 0) playlist.size - 1 else currentIndex - 1
        val prevSong = playlist[currentIndex]
        loadSong(prevSong)
        audioPlayerManager.play()
        _uiState.update { it.copy(isPlaying = true, progress = 0) }
    }
    private fun setupVisualizer() {
        audioPlayerManager.setupVisualizer()
    }

    private fun loadSong(name: String) {
        val audioInfo = audioPlayerManager.getAudioInfo(name)
        audioPlayerManager.load(name)
        val bandCount = audioPlayerManager.getBandCount()
        val initialBands = List(bandCount.toInt()) { 0f }
        val bandLevels = audioPlayerManager.getBandRange()
        _uiState.update {
            it.copy(
                audioInfo = audioInfo,
                bandLevels = initialBands,
                bandRange = bandLevels.toList()
            )
        }

    }

    private fun playPause() {
        if (audioPlayerManager.isPlaying() && _uiState.value.progress != 100) audioPlayerManager.pause() else audioPlayerManager.play()
        _uiState.update { it.copy(isPlaying = audioPlayerManager.isPlaying()) }
    }

    private fun seekTo(value: Float) {
        audioPlayerManager.seekTo(value.toInt())
    }

    private fun changeBand(index: Int, value: Float) {
        _uiState.update {
            val newBandLevels = it.bandLevels.toMutableList()
            newBandLevels[index] = value
            it.copy(bandLevels = newBandLevels)
        }
        audioPlayerManager.setBandLevel(index.toShort(), value.toInt().toShort())
    }

    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (isActive) {
                val currentPosition = audioPlayerManager.currentPosition()
                val duration = _uiState.value.audioInfo?.duration
                _uiState.update {
                    it.copy(
                        progress = audioPlayerManager.currentPosition(),
                        waveForm = if (audioPlayerManager.isPlaying()) audioPlayerManager.getWaveform()
                            .toList() else it.waveForm
                    )
                }
                if (duration in 1..currentPosition) {
                    onEvent(AudioPlayerEvent.PlayNext)
                }
                delay(300)
            }
        }
    }

    fun onEvent(audioPlayerEvent: AudioPlayerEvent) {
        when (audioPlayerEvent) {
            is AudioPlayerEvent.ChangeBand -> changeBand(index = audioPlayerEvent.index, value = audioPlayerEvent.value)
            is AudioPlayerEvent.LoadSong -> loadSong(name = audioPlayerEvent.name)
            is AudioPlayerEvent.SeekTo -> seekTo(value = audioPlayerEvent.value)
            AudioPlayerEvent.StartProgressUpdater -> startProgressUpdater()
            AudioPlayerEvent.PlayPause -> playPause()
            AudioPlayerEvent.PlayNext -> playNext()
            AudioPlayerEvent.PlayPrevious -> playPrevious()
            AudioPlayerEvent.SetupVisualizer -> setupVisualizer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerManager.release()
    }

}

