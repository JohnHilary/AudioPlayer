package com.john.audioplayer.view.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.john.audioplayer.theme.AudioPlayerTheme
import com.john.audioplayer.viewmodel.AudioPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioPlayerTheme {
                val viewModel = hiltViewModel<AudioPlayerViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle()
                val lifecycleOwner = LocalLifecycleOwner.current

                DisposableEffect (lifecycleOwner) {
                    lifecycleOwner.lifecycle.addObserver(viewModel)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(viewModel)
                    }
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PlayerScreen(
                        onEvent = {
                            viewModel.onEvent(it)
                        },
                        uiState = uiState.value,
                        modifier = Modifier.Companion
                            .padding(innerPadding)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

