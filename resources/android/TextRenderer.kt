package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.argbToComposeColor

object TextRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val text = p.getString("text")
        val fontSize = p.getFloat("font_size", 16f)
        val fontWeight = resolveFontWeight(p.getInt("font_weight"))
        val maxLines = p.getInt("max_lines")
        val textAlign = resolveTextAlign(p.getInt("text_align"))

        val isDark = isSystemInDarkTheme()
        val darkColor = if (isDark) p.getColor("dark_color", 0) else 0
        val textArgb = if (darkColor != 0) darkColor else p.getColor("color", 0xFF000000.toInt())

        Text(
            text = text,
            modifier = modifier,
            color = argbToComposeColor(textArgb),
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            textAlign = textAlign,
            maxLines = if (maxLines > 0) maxLines else Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis,
            // Android adds extra "font padding" above/below glyphs by default,
            // which shifts text within its box — so it doesn't vertically center
            // against icons (e.g. the X engagement row) and reads looser than
            // iOS. Disable it and trim line-height padding so text hugs its
            // glyphs like iOS.
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            )
        )
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