package com.john.audioplayer.audio

import android.net.Uri

sealed interface FileType {
    data class ASSET(val file: String) : FileType
    data class URI(val file: Uri) : FileType
}