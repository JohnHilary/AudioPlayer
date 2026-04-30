package com.john.audioplayer.view.screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.john.audioplayer.audio.FileType
import com.john.audioplayer.navigation.NavigationHost
import com.john.audioplayer.theme.AudioPlayerTheme
import com.john.audioplayer.view.events.AudioPlayerEvent
import com.john.audioplayer.viewmodel.AudioPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: AudioPlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) {
            handleIntent(intent)
        }

        setContent {
            AudioPlayerTheme {
                NavigationHost(viewModel = viewModel)
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }
    private fun handleIntent(intent: Intent?) {
      if (intent?.action == Intent.ACTION_VIEW) {
            intent.data?.let {
                viewModel.onEvent(AudioPlayerEvent.LoadSong(fileType = FileType.URI(file = it)))
            }
      }
    }
}

