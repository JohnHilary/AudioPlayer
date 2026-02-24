package com.john.audioplayer.view.screen

import android.Manifest
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.john.audioplayer.R
import com.john.audioplayer.state.AudioPlayerScreenUiState
import com.john.audioplayer.view.CheckPermission
import com.john.audioplayer.view.events.AudioPlayerEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    uiState: AudioPlayerScreenUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AudioPlayerEvent) -> Unit,
) {
    var hasAudioPermission by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    CheckPermission(
        permission = Manifest.permission.RECORD_AUDIO
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            onEvent(AudioPlayerEvent.SetupVisualizer)
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message = "Permission required to display the visualizer.")
            }
        }
    }

    val amplitudes by remember(uiState.waveForm) {
        derivedStateOf {
            uiState.waveForm
                .take(40)
                .map { kotlin.math.abs(it.toInt()) / 128f }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val albumArtBitmap = uiState.audioInfo?.albumArt
        Image(
            bitmap = albumArtBitmap?.asImageBitmap()
                ?: ImageBitmap.imageResource(R.drawable.app_logo),
            contentDescription = "Album Art",
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(Modifier.height(16.dp))
        Text(
            text = uiState.audioInfo?.title ?: "Untitled Audio",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis

        )
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(modifier = Modifier.height(2.dp))
        Spacer(Modifier.height(8.dp))
        Text(
            text = uiState.audioInfo?.artist ?: "Unknow Artist",
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = uiState.audioInfo?.album ?: "Unknow Album",
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        uiState.audioInfo?.duration?.let {
            Slider(
                value = uiState.progress.toFloat(), onValueChange = {
                    onEvent(AudioPlayerEvent.SeekTo(it))
                }, valueRange = 0f..uiState.audioInfo.duration.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = remember { MutableInteractionSource() },
                        modifier = Modifier.size(10.dp)
                    )
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier.height(4.dp)
                    )
                }
            )
            Text(
                text = "${formatTime(uiState.progress)} / ${formatTime(uiState.audioInfo.duration)}",
                fontSize = 18.sp
            )
        }
        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = { onEvent(AudioPlayerEvent.PlayPrevious) }
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.primary

                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.primary, CircleShape)
                    .size(100.dp)
                    .padding(16.dp),
                onClick = { onEvent(AudioPlayerEvent.PlayPause) }
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = if (uiState.isPlaying && uiState.progress != 100)
                        Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = { onEvent(AudioPlayerEvent.PlayNext) }
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        val color = MaterialTheme.colorScheme.onBackground
        if (hasAudioPermission) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                val barWidth = size.width / amplitudes.size
                val centerY = size.height / 2
                amplitudes.forEachIndexed { index, amplitude ->
                    val scaled = amplitude * centerY
                    drawLine(
                        color = color,
                        start = Offset(x = index * barWidth, y = centerY - scaled),
                        end = Offset(x = index * barWidth, y = centerY + scaled),
                        strokeWidth = barWidth * 0.25f
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        HorizontalDivider(modifier = Modifier.height(2.dp))
        Spacer(Modifier.height(20.dp))

        EqualizerUI(bands = uiState.bandLevels, range = uiState.bandRange) { index, value ->
            onEvent(AudioPlayerEvent.ChangeBand(index, value))
        }

        Spacer(Modifier.height(20.dp))
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
        isPlaying = true,
        waveForm = List(16) { 1 },
        bandLevels = List(5) { 0.5f },
    )

    PlayerScreen(
        onEvent = {}, uiState = audioPlayerScreenUiState, snackbarHostState = SnackbarHostState()
    )
}