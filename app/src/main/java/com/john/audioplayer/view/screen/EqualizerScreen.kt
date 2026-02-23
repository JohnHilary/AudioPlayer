package com.john.audioplayer.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.john.audioplayer.state.AudioPlayerScreenUiState

@Composable
fun EqualizerUI(
    bands: List<Float>,
    range: List<Short>,
    onChange: (Int, Float) -> Unit
) {
    val labels = listOf("60Hz", "230Hz", "910Hz", "3kHz", "14kHz")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        bands.forEachIndexed { index, value ->
            key(index) {

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val label = labels.getOrElse(index) { "Band ${index + 1}" }

                    Text(
                        text = label,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Slider(
                            value = value,
                            onValueChange = { onChange(index, it) },
                            valueRange = range[0].toFloat()..range[1].toFloat(),
                            modifier = Modifier
                                .graphicsLayer {
                                    rotationZ = 270f
                                    transformOrigin = TransformOrigin(0f, 0f)
                                }
                                .layout { measurable, constraints ->
                                    val placeable = measurable.measure(
                                        Constraints(
                                            minWidth = constraints.minHeight,
                                            maxWidth = constraints.maxHeight,
                                            minHeight = constraints.minWidth,
                                            maxHeight = constraints.maxHeight
                                        )
                                    )
                                    layout(placeable.height, placeable.width) {
                                        placeable.place(-placeable.width, 0)
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EqualizerUIPreview() {
    val audioPlayerScreenUiState = AudioPlayerScreenUiState(
        progress = 30,
        isPlaying = true,
        waveForm = List(16) { 1 },
        bandLevels = List(5) { 0.5f })

    EqualizerUI(
        bands = audioPlayerScreenUiState.bandLevels,
        range = emptyList(),
        onChange = { p0: Int, p2: Float ->
        }
    )

}