package com.hunglvv.stickmananimation.library.components

import android.net.Uri
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.hunglvv.stickmananimation.library.R

data class CommonVideo(
    val thumbnail: String = "",
    val source: String = ""
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoPager(
    modifier: Modifier = Modifier,
    videos: List<CommonVideo>,
    initialPage: Int? = 0,
    verticalPager: Boolean = true,
    onPageSelected: (Int) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage ?: 0,
        initialPageOffsetFraction = 0f
    ) {
        videos.size
    }

    val fling = PagerDefaults.flingBehavior(
        state = pagerState, lowVelocityAnimationSpec = tween(
            easing = LinearEasing, durationMillis = 300
        )
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { index ->
            onPageSelected(index)
        }
    }

    if (verticalPager) {
        VerticalPager(
            state = pagerState,
            flingBehavior = fling,
            beyondBoundsPageCount = 1,
            modifier = modifier
        ) {
            PageContent(
                source = videos[it].source,
                videos[it].thumbnail,
                pagerState,
                it,
                R.drawable.ic_play
            )
        }
    } else {
        HorizontalPager(
            state = pagerState,
            flingBehavior = fling,
            beyondBoundsPageCount = 1,
            modifier = modifier
        ) {
            PageContent(
                source = videos[it].source,
                videos[it].thumbnail,
                pagerState,
                it,
                R.drawable.ic_play
            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PageContent(
    source: String,
    thumbnail: String,
    pagerState: PagerState,
    pageIndex: Int,
    @DrawableRes resPlayIcon: Int
) {

    var pauseButtonVisibility by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        VideoPlayer(source, thumbnail, pagerState, pageIndex, onSingleTap = {
            pauseButtonVisibility = it.isPlaying
            it.playWhenReady = !it.isPlaying
        },
            onVideoDispose = { pauseButtonVisibility = false },
            onVideoGoBackground = { pauseButtonVisibility = false }

        )

        AnimatedVisibility(
            visible = pauseButtonVisibility,
            enter = scaleIn(spring(Spring.DampingRatioMediumBouncy), initialScale = 1.5f),
            exit = scaleOut(tween(150)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                painter = painterResource(id = resPlayIcon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(36.dp)
            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoPlayer(
    source: String,
    thumbnail: String,
    pagerState: PagerState,
    pageIndex: Int,
    onSingleTap: (exoPlayer: ExoPlayer) -> Unit,
    onVideoDispose: () -> Unit = {},
    onVideoGoBackground: () -> Unit = {}
) {
    val context = LocalContext.current

    var isFirstFrameLoad by remember { mutableStateOf(false) }


    if (pagerState.settledPage == pageIndex) {
        val exoPlayer = remember(context) {
            ExoPlayer.Builder(context).setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(), true
            ).build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                setMediaItem(MediaItem.fromUri(Uri.parse(source)))
                playWhenReady = true
                prepare()
                addListener(object : Player.Listener {
                    override fun onRenderedFirstFrame() {
                        super.onRenderedFirstFrame()
                        isFirstFrameLoad = true
                    }
                })
            }
        }

        val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
        DisposableEffect(key1 = lifecycleOwner) {
            val lifeCycleObserver = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_STOP -> {
                        exoPlayer.pause()
                        onVideoGoBackground()
                    }

                    Lifecycle.Event.ON_START -> exoPlayer.play()
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(lifeCycleObserver)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(lifeCycleObserver)
            }
        }


        AndroidView(factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }, modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                onSingleTap(exoPlayer)
            })
        })

        DisposableEffect(Unit) {

            onDispose {
                isFirstFrameLoad = false
                exoPlayer.release()
                onVideoDispose()
            }
        }

    }

    if (!isFirstFrameLoad) {
        GlideImage(
            imageModel = { thumbnail },
            modifier = Modifier.fillMaxSize(),
            imageOptions = ImageOptions(contentScale = ContentScale.Fit)
        )
    }
}