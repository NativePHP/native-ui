package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode

object TextRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val text = p.getString("text")
        if (text.isEmpty()) return

        Text(
            text = text,
            modifier = modifier.then(applyClickModifier(node)),
            color = Color(p.getColor("color", 0xFF000000.toInt())),
            fontSize = p.getFloat("font_size", 16f).sp,
            fontWeight = resolveFontWeight(p.getInt("font_weight")),
            textAlign = resolveTextAlign(p.getInt("text_align")),
            maxLines = p.getInt("max_lines").let { if (it > 0) it else Int.MAX_VALUE },
            overflow = TextOverflow.Ellipsis
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

    private fun resolveFontWeight(weight: Int): FontWeight {
        return when (weight) {
            1 -> FontWeight.Thin
            2 -> FontWeight.Light
            3 -> FontWeight.Normal
            4 -> FontWeight.Medium
            5 -> FontWeight.SemiBold
            6 -> FontWeight.Bold
            7 -> FontWeight.ExtraBold
            else -> FontWeight.Normal
        }
    }

    private fun resolveTextAlign(align: Int): TextAlign {
        return when (align) {
            0 -> TextAlign.Start
            1 -> TextAlign.Center
            2 -> TextAlign.End
            else -> TextAlign.Start
        }
    }
}