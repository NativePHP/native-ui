package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.NativeUINode

object ActivityIndicatorRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val sizeDp = when (p.getInt("size")) {
            1 -> 48.dp  // large
            2 -> 20.dp  // small
            else -> 32.dp // medium
        }

        CircularProgressIndicator(
            modifier = modifier.size(sizeDp),
            color = Color(p.getColor("color", 0xFF6200EE.toInt()))
        )
    }
}
