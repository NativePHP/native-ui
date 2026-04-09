package com.nativephp.plugins.compose_ui.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.argbToComposeColor

object ImageRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val src = p.getString("src")
        val fit = p.getInt("fit")
        val tintArgb = p.getColor("tint_color", 0)

        if (src.isNotEmpty()) {
            AsyncImage(
                model = src,
                contentDescription = null,
                modifier = modifier,
                contentScale = resolveContentScale(fit),
                colorFilter = if (tintArgb != 0) {
                    ColorFilter.tint(argbToComposeColor(tintArgb))
                } else null
            )
        }
    }
}

private fun resolveContentScale(fit: Int): ContentScale {
    return when (fit) {
        0 -> ContentScale.None
        1 -> ContentScale.Fit
        2 -> ContentScale.Crop
        3 -> ContentScale.FillBounds
        4 -> ContentScale.Fit
        else -> ContentScale.Fit
    }
}
