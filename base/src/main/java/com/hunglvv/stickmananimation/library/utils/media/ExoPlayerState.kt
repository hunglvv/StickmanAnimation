package com.hunglvv.stickmananimation.library.utils.media

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource

@SuppressLint("UnsafeOptInUsageError")
class ExoPlayerState(
    context: Context,
    audioAttributes: AudioAttributes,
) : ExoPlayer by ExoPlayer.Builder(context).setAudioAttributes(audioAttributes, true).build(),
    Player.Listener {
    @get:JvmName("playing")
    var isPlaying by mutableStateOf(false)
        private set

    init {
        addListener(this)
    }

    fun playNew() {
        seekTo(0)
        playWhenReady = true
    }

    fun prepareMedia(url: String, cache: SimpleCache?) {
        val media = MediaItem.fromUri(url.toUri())
        if (null != cache) {
            val mediaSource = ProgressiveMediaSource.Factory(
                CacheDataSource.Factory()
                    .setCache(cache)
                    .setUpstreamDataSourceFactory(
                        DefaultHttpDataSource.Factory()
                            .setAllowCrossProtocolRedirects(true)
                    )
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            ).createMediaSource(media)
            setMediaSource(mediaSource)
        } else {
            setMediaItem(media)
        }

        playWhenReady = false
        prepare()
    }

    fun togglePlayingState() {
        seekTo(0)
        playWhenReady = !playWhenReady
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        this.isPlaying = isPlaying
    }
}

@Composable
fun rememberExoPlayerState(
    lifecycleAware: Boolean = true,
    onResume: () -> Unit = {},
    onPause: () -> Unit = {}
): ExoPlayerState {
    val context = LocalContext.current
    val exoPlayerState = remember {
        ExoPlayerState(
            context, AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build()
        )
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    if (lifecycleAware) {
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> onResume()
                    Lifecycle.Event.ON_PAUSE -> onPause()
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                exoPlayerState.release()
            }
        }
    }

    return exoPlayerState
}