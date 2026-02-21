package com.john.audioplayer.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.john.audioplayer.view.events.AudioPlayerEvent
import com.john.audioplayer.audio.AudioPlayerManager
import com.john.audioplayer.state.AudioPlayerScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val audioPlayerManager: AudioPlayerManager,
    private val _uiState: MutableStateFlow<AudioPlayerScreenUiState>
) : ViewModel(), DefaultLifecycleObserver {

    val uiState = _uiState.asStateFlow()

    init {
        loadSong("music.mp3")
        startProgressUpdater()
    }

    private fun loadSong(name: String) {
        audioPlayerManager.load(name)
        _uiState.update {
            it.copy(duration = audioPlayerManager.duration())
        }
        val bandCount = audioPlayerManager.getBandCount()
        val initialBands = List(bandCount.toInt()) { 0f }
        _uiState.update {
            it.copy(bandLevels = initialBands)
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
            while (true) {
                _uiState.update {
                    it.copy(
                        progress = audioPlayerManager.currentPosition(),
                        waveForm = audioPlayerManager.getWaveform().toList()
                    )
                }
                delay(300)
            }
        }
    }

    fun onEvent(audioPlayerEvent: AudioPlayerEvent) {
        when (audioPlayerEvent) {
            is AudioPlayerEvent.ChangeBand -> {
                changeBand(index = audioPlayerEvent.index, value = audioPlayerEvent.value)
            }

            is AudioPlayerEvent.LoadSong -> {
                loadSong(name = audioPlayerEvent.name)
            }

            is AudioPlayerEvent.SeekTo -> {
                seekTo(value = audioPlayerEvent.value)
            }

            AudioPlayerEvent.StartProgressUpdater -> {
                startProgressUpdater()
            }

            AudioPlayerEvent.PlayPause -> {
                playPause()

            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        audioPlayerManager.release()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        if (audioPlayerManager.isPlaying()) {
            audioPlayerManager.pause()
            _uiState.update { it.copy(isPlaying = false) }
        }
    }
}

