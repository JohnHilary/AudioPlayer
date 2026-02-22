package com.john.audioplayer.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.john.audioplayer.view.screen.PlayerScreen
import com.john.audioplayer.viewmodel.AudioPlayerViewModel
import kotlinx.serialization.Serializable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = PlayerScreen,
            modifier = Modifier.padding(it)
        ) {

            composable<PlayerScreen> {
                val viewModel = hiltViewModel<AudioPlayerViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle()
                PlayerScreen(
                    onEvent = { event ->
                        viewModel.onEvent(event)
                    },
                    uiState = uiState.value,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

    }

}


@Serializable
data object PlayerScreen