package com.john.audioplayer.view.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun EqualizerUI(
    bands: List<Float>,
    onChange: (Int, Float) -> Unit
) {
    Row {
        bands.forEachIndexed { index, value ->
            Slider(
                value = value,
                onValueChange = { onChange(index, it) },
                valueRange = -1500f..1500f,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
