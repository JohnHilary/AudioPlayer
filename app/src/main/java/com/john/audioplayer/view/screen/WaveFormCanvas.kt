package com.john.audioplayer.view.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WaveformCanvas(data: ByteArray, progress: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val width = size.width
        val height = size.height
        val halfHeight = height / 2

        data.forEachIndexed { i, byte ->
            val x = i * width / data.size
            val normalized = byte / 128f
            val y = halfHeight - (normalized * halfHeight)

            drawLine(
                color = if (x < width * progress) Color.Green else Color.Gray,
                start = Offset(x, halfHeight),
                end = Offset(x, y),
                strokeWidth = 1f
            )
        }
    }
}

