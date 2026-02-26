package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode

object ImageRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val src = p.getString("src")
        val fit = p.getInt("fit")
        val tintColor = p.getColor("tint_color", 0)

        val contentScale = when (fit) {
            0 -> ContentScale.None
            1 -> ContentScale.Fit
            2 -> ContentScale.Crop
            3 -> ContentScale.FillBounds
            4 -> ContentScale.Inside
            else -> ContentScale.Fit
        }

        val colorFilter = if (tintColor != 0) ColorFilter.tint(Color(tintColor)) else null

        SubcomposeAsyncImage(
            model = src,
            contentDescription = null,
            modifier = modifier.then(applyClickModifier(node)),
            contentScale = contentScale,
            colorFilter = colorFilter,
            loading = {
                Box(
                    modifier = Modifier.background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier,
                        strokeWidth = 2.dp,
                        color = Color.Gray
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier.background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = src.takeLast(20),
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        )
    }

    private fun applyClickModifier(node: NativeUINode): Modifier {
        return if (node.onPress != 0) {
            Modifier.clickable {
                NativeUIBridge.sendPressEvent(node.onPress, node.id)
            }
        } else {
            Modifier
        }
    }
}
