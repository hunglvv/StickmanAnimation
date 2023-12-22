package com.hunglvv.stickmananimation.library.components


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Picker(
    initialSecond: Int = 0,
    minutes: List<Int> = listOf(0, 1, 2, 3, 4),
    seconds: List<Int> = (0..59).toList(),
    visibleItemsCount: Int = 5,
    highlightColor: Color = Color(0xFFFFF6E5),
    timeTextStyle: TextStyle,
    labelStyle: TextStyle,
    onTimeChange: (second: Int) -> Unit
) {
    val startIndexMinute = TimeUnit.SECONDS.toMinutes(initialSecond.toLong()) % 60
    val startIndexSecond = initialSecond % 60

    var minuteState by remember { mutableIntStateOf(startIndexMinute.toInt()) }
    var secondState by remember { mutableIntStateOf(startIndexSecond) }

    val visibleItemsMiddle = visibleItemsCount / 2
    val listScrollCount = Integer.MAX_VALUE
    val listScrollMiddle = listScrollCount / 2
    val listStartHourIndex =
        listScrollMiddle - listScrollMiddle % minutes.size - visibleItemsMiddle + startIndexMinute.toInt()
    val listStartSecondIndex =
        listScrollMiddle - listScrollMiddle % seconds.size - visibleItemsMiddle + startIndexSecond


    val listMinuteState = rememberLazyListState(initialFirstVisibleItemIndex = listStartHourIndex)
    val listSecondState = rememberLazyListState(initialFirstVisibleItemIndex = listStartSecondIndex)
    val flingMinuteBehavior = rememberSnapFlingBehavior(lazyListState = listMinuteState)
    val flingSecondBehavior = rememberSnapFlingBehavior(lazyListState = listSecondState)

    var itemHeightPixels by remember { mutableIntStateOf(0) }
    val itemHeightDp = pixelsToDp(itemHeightPixels)

    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.5f to Color.Black,
            1f to Color.Transparent
        )
    }

    fun getMinute(index: Int) = minutes[index % minutes.size]
    fun getSecond(index: Int) = seconds[index % seconds.size]

    LaunchedEffect(listMinuteState) {
        snapshotFlow { listMinuteState.firstVisibleItemIndex }
            .map { index -> getMinute(index + visibleItemsMiddle) }
            .distinctUntilChanged()
            .collect { item ->
                minuteState = item
                onTimeChange(item * 60 + secondState)
            }
    }

    LaunchedEffect(listSecondState) {
        snapshotFlow { listSecondState.firstVisibleItemIndex }
            .map { index -> getSecond(index + visibleItemsMiddle) }
            .distinctUntilChanged()
            .collect { item ->
                secondState = item
                onTimeChange(minuteState * 60 + item)
            }
    }

    Box(
        modifier = Modifier.background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(240.dp, 36.dp)
                .background(highlightColor, RoundedCornerShape(4.dp))
                .align(Alignment.Center)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyColumn(
                state = listMinuteState,
                flingBehavior = flingMinuteBehavior,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .height(itemHeightDp * visibleItemsCount)
                    .fadingEdge(fadingEdgeGradient)
            ) {
                items(listScrollCount) { index ->
                    Text(
                        text = "${getMinute(index)}",
                        style = timeTextStyle,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 24.dp)
                            .onSizeChanged { size -> itemHeightPixels = size.height }
                    )
                }
            }
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = stringResource(com.hunglvv.stickmananimation.library.R.string.min),
                style = labelStyle
            )
            Spacer(modifier = Modifier.size(18.dp))
            Text(
                text = ":",
                style = timeTextStyle
            )
            Spacer(modifier = Modifier.size(45.dp))
            LazyColumn(
                state = listSecondState,
                flingBehavior = flingSecondBehavior,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .height(itemHeightDp * visibleItemsCount)
                    .fadingEdge(fadingEdgeGradient)
            ) {
                items(listScrollCount) { index ->
                    Text(
                        text = "${getSecond(index)}",
                        style = timeTextStyle,
                        modifier = Modifier.defaultMinSize(minWidth = 24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = stringResource(com.hunglvv.stickmananimation.library.R.string.sec),
                style = labelStyle
            )
        }


    }

}

private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }
