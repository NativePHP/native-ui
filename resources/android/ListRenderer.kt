package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.NativeElementBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.NodeView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ListRenderer {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val horizontal = node.props.getBool("horizontal")
        val separator = node.props.getBool("separator")
        val onRefreshCb = node.props.getCallbackId("on_refresh")
        val onEndReachedCb = node.props.getCallbackId("on_end_reached")

        val scrollState = rememberLazyListState()
        val isRefreshing = remember { mutableStateOf(false) }
        val endReachedFired = remember { mutableStateOf(false) }

        // Detect end reached — fire when within 3 items of the bottom
        if (onEndReachedCb != 0) {
            LaunchedEffect(scrollState) {
                snapshotFlow {
                    val info = scrollState.layoutInfo
                    val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: 0
                    val total = info.totalItemsCount
                    total > 0 && lastVisible >= total - 3
                }.collect { nearEnd ->
                    if (nearEnd && !endReachedFired.value) {
                        endReachedFired.value = true
                        NativeElementBridge.sendPressEvent(onEndReachedCb, node.id)
                    } else if (!nearEnd) {
                        endReachedFired.value = false
                    }
                }
            }
        }

        val keyboardController = LocalSoftwareKeyboardController.current
        val dismissKeyboardModifier = Modifier.pointerInput(Unit) {
            detectVerticalDragGestures(onDragStart = { keyboardController?.hide() }) { _, _ -> }
        }

        val listContent: @Composable () -> Unit = {
            if (horizontal) {
                LazyRow(modifier = if (onRefreshCb != 0) Modifier else modifier, state = scrollState) {
                    node.children.forEachIndexed { index, child ->
                        item(key = child.id) {
                            NodeView(node = child)
                        }
                    }
                }
            } else {
                LazyColumn(modifier = (if (onRefreshCb != 0) Modifier else modifier).then(dismissKeyboardModifier), state = scrollState) {
                    node.children.forEachIndexed { index, child ->
                        item(key = child.id) {
                            val deleteCb = child.props.getCallbackId("on_swipe_delete")
                            if (deleteCb != 0) {
                                SwipeToDeleteRow(
                                    nodeKey = child,
                                    onDelete = { NativeElementBridge.sendPressEvent(deleteCb, child.id) }
                                ) {
                                    NodeView(node = child)
                                }
                            } else {
                                NodeView(node = child)
                            }
                            if (separator && index < node.children.size - 1) {
                                HorizontalDivider(color = Color(0xFFE0E0E0))
                            }
                        }
                    }
                }
            }
        }

        // Pull-to-refresh wrapper
        if (onRefreshCb != 0) {
            val refreshState = rememberPullToRefreshState()

            val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

            PullToRefreshBox(
                isRefreshing = isRefreshing.value,
                onRefresh = {
                    isRefreshing.value = true
                    NativeElementBridge.sendPressEvent(onRefreshCb, node.id)
                    coroutineScope.launch {
                        delay(1500)
                        isRefreshing.value = false
                    }
                },
                state = refreshState,
                modifier = modifier
            ) {
                listContent()
            }
        } else {
            listContent()
        }
    }
}

/**
 * Simple swipe-to-reveal-delete row. Swipe left to reveal a Delete button.
 * Tap the button to fire the callback. No persistent dismiss state.
 */
@Composable
private fun SwipeToDeleteRow(
    nodeKey: Any,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val offsetX = remember(nodeKey) { mutableStateOf(0f) }
    val deleteWidth = 80.dp
    val density = androidx.compose.ui.platform.LocalDensity.current
    val deleteWidthPx = with(density) { deleteWidth.toPx() }
    val animatedOffset = animateFloatAsState(targetValue = offsetX.value, label = "swipe")
    val offsetDp = with(density) { animatedOffset.value.toDp() }

    Box(
        Modifier
            .fillMaxWidth()
            .clipToBounds()
    ) {
        // Delete button — aligned to the right, always present but hidden behind content
        Box(
            Modifier
                .matchParentSize()
                .background(Color.Red)
                .clickable {
                    offsetX.value = 0f
                    onDelete()
                },
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(Modifier.width(deleteWidth), contentAlignment = Alignment.Center) {
                Text("Delete", color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        }

        // Foreground content slides left to reveal delete
        Box(
            Modifier
                .fillMaxWidth()
                .offset(x = offsetDp)
                .background(Color.White)
                .pointerInput(nodeKey) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX.value < -deleteWidthPx / 2) {
                                offsetX.value = -deleteWidthPx
                            } else {
                                offsetX.value = 0f
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX.value = (offsetX.value + dragAmount).coerceIn(-deleteWidthPx, 0f)
                        }
                    )
                }
        ) {
            content()
        }
    }
}
