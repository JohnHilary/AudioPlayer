package com.john.audioplayer.di

import android.content.Context
import com.john.audioplayer.audio.AudioPlayerManager
import com.john.audioplayer.state.AudioPlayerScreenUiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideAudioPlayerManager(
        @ApplicationContext context: Context
    ): AudioPlayerManager {
        return AudioPlayerManager(context)
    }

    @Provides
    @Singleton
    fun provideAudioPlayerStateFlow(): MutableStateFlow<AudioPlayerScreenUiState> {
        return MutableStateFlow(AudioPlayerScreenUiState())
    }
}