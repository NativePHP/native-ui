package com.nativephp.plugins.compose_ui.ui

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.nativephp.mobile.ui.nativerender.NativeUINode

object ProgressBarRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        LinearProgressIndicator(
            progress = { p.getFloat("value").coerceIn(0f, 1f) },
            modifier = modifier,
            color = Color(p.getColor("color", 0xFF6200EE.toInt())),
            trackColor = Color(p.getColor("track_color", 0xFFE0E0E0.toInt()))
        )
    }
}
