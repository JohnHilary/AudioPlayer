package com.john.audioplayer.view.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.john.audioplayer.view.events.AudioPlayerEvent
import com.john.audioplayer.state.AudioPlayerScreenUiState

@Composable
fun PlayerScreen(
    onEvent: (AudioPlayerEvent) -> Unit,
    uiState: AudioPlayerScreenUiState,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Simple Audio Player", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        Slider(
            value = uiState.progress.toFloat(),
            onValueChange = {
                onEvent(AudioPlayerEvent.SeekTo(it))
            },
            valueRange = 0f..uiState.duration.toFloat()
        )

        Text("${formatTime(uiState.progress)} / ${formatTime(uiState.duration)}")
        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            onEvent(AudioPlayerEvent.PlayPause)
        }) {
            Text(if (uiState.isPlaying && uiState.progress != 100) "Pause" else "Play")
        }

        Spacer(Modifier.height(20.dp))

        WaveformCanvas(
            uiState.waveForm.toByteArray(),
            uiState.progress.toFloat() / uiState.duration
        )

        Spacer(Modifier.height(20.dp))

        EqualizerUI(uiState.bandLevels) { index, value ->
            onEvent(AudioPlayerEvent.ChangeBand(index, value))
        }

        Spacer(Modifier.height(20.dp))

        PresetButtons(onEvent)
    }
}

fun formatTime(ms: Int): String {
    val minutes = (ms / 1000) / 60
    val seconds = (ms / 1000) % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun PlayerScreenPreview() {
    val audioPlayerScreenUiState = AudioPlayerScreenUiState(
        progress = 30,
        duration = 120,
        isPlaying = true,
        waveForm = List(16) { 1 },
        bandLevels = List(5) { 0.5f }
    )

    PlayerScreen(
        onEvent = {},
        uiState = audioPlayerScreenUiState
    )
}