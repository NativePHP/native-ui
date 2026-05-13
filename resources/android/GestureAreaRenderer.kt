package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.NodeView
import com.nativephp.mobile.ui.nativerender.SharedValueStore

/**
 * Captures a vertical drag and writes the cumulative translation into
 * the bound `SharedValue` on `SharedValueStore`. Children render
 * normally — the gesture wraps the whole content frame.
 *
 * Driven by props:
 *   - `pan-y-id`       (int) — id of the SharedValue receiving updates.
 *   - `pan-y-initial`  (float) — initial value to seed the store with.
 *
 * Per-frame value updates happen on the Compose render thread; no PHP
 * involvement until a discrete event (when we wire @drag-end later).
 */
object GestureAreaRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val panYId = node.props.getInt("pan-y-id", 0)
        val panYInitial = node.props.getFloat("pan-y-initial", 0f)

        LaunchedEffect(panYId) {
            if (panYId != 0) {
                SharedValueStore.seed(panYId, panYInitial)
            }
        }

        Box(
            modifier = modifier.pointerInput(panYId) {
                if (panYId == 0) return@pointerInput
                detectVerticalDragGestures(
                    onDragStart = { /* no-op — store already holds the running value */ },
                    onDragEnd = { /* @drag-end callback wired in 3b */ },
                    onDragCancel = { /* same */ },
                ) { _, dragAmount ->
                    // dragAmount is in raw pixels; convert to dp so the
                    // SharedValue stays density-independent and the
                    // user's `interpolate([0, 200], ...)` formulas match
                    // iOS point-based behavior. PointerInputScope
                    // extends Density, so .toDp() is in scope.
                    val deltaDp = dragAmount.toDp().value
                    val current = SharedValueStore.valueOf(panYId)
                    SharedValueStore.set(panYId, current + deltaDp)
                }
            }
        ) {
            node.children.forEach { child ->
                NodeView(node = child)
            }
        }
    }
}
