package com.john.audioplayer.model

import android.graphics.Bitmap

data class AudioInfo(
    val title: String?,
    val artist: String?,
    val album: String?,
    val duration: Int,
    val albumArt: Bitmap?
)