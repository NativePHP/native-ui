package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.argbToComposeColor

object SpacerRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        Spacer(modifier = modifier)
    }
}

object DividerRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val borderArgb = node.style?.borderColor ?: 0
        val color = if (borderArgb != 0) argbToComposeColor(borderArgb) else Color(0xFFE0E0E0)
        HorizontalDivider(modifier = modifier, color = color)
    }
}

object RectRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        Box(modifier = modifier)
    }
}

object CircleRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        Box(modifier = modifier)
    }
}

object LineRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val borderArgb = node.style?.borderColor ?: 0
        val color = if (borderArgb != 0) argbToComposeColor(borderArgb) else Color(0xFFE0E0E0)
        val strokeWidth = node.style?.borderWidth ?: 1f

        Canvas(modifier = modifier.fillMaxWidth().height(strokeWidth.dp)) {
            drawLine(
                color = color,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = strokeWidth
            )
        }
    }
}
