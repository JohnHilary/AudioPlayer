package com.john.audioplayer.view.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.john.audioplayer.view.events.AudioPlayerEvent

@Composable
fun PresetButtons(onEvent: (AudioPlayerEvent) -> Unit) {

    val presets = mapOf(
        "Flat" to listOf(0f, 0f, 0f, 0f, 0f),
        "Rock" to listOf(1000f, 500f, 0f, 500f, 1000f),
        "Jazz" to listOf(0f, 500f, 1000f, 500f, 0f),
        "Pop" to listOf(500f, 1000f, 500f, 1000f, 500f)
    )

    Row {
        presets.forEach { (name, values) ->
            Button(onClick = {
                values.forEachIndexed { index, value ->
                    onEvent(AudioPlayerEvent.ChangeBand(index, value))
                }
            }) {
                Text(name)
            }
        }
    }
}
