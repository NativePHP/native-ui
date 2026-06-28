package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.argbToComposeColor

object TextRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val text = applyTransform(p.getString("text"), p.getInt("text_transform"))
        val fontSize = p.getFloat("font_size", 16f)
        val fontWeight = resolveFontWeight(p.getInt("font_weight"))
        val fontStyle = resolveFontStyle(p.getInt("font_style"))
        val fontFamily = resolveFontFamily(p.getInt("font_family"))
        val textDecoration = resolveDecoration(p.getInt("underline"), p.getInt("line_through"))
        val letterSpacingEm = p.getFloat("letter_spacing", 0f)
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
            fontStyle = fontStyle,
            textDecoration = textDecoration,
            letterSpacing = if (letterSpacingEm != 0f) letterSpacingEm.em else TextUnit.Unspecified,
            textAlign = textAlign,
            maxLines = if (maxLines > 0) maxLines else Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis,
            // Android adds extra "font padding" above/below glyphs by default,
            // which shifts text within its box — so it doesn't vertically center
            // against icons (e.g. the X engagement row) and reads looser than
            // iOS. Disable it and trim line-height padding so text hugs its
            // glyphs like iOS.
            // fontFamily lives here (not as a loose param) so it's guaranteed to
            // survive into the final TextStyle.
            style = TextStyle(
                fontFamily = fontFamily,
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

private fun resolveFontStyle(style: Int): FontStyle {
    return if (style == 1) FontStyle.Italic else FontStyle.Normal
}

// 0/absent = default (sans); only override for serif/mono so a custom default
// font isn't clobbered.
private fun resolveFontFamily(family: Int): FontFamily? = when (family) {
    1 -> FontFamily.Serif
    2 -> FontFamily.Monospace
    else -> null
}

private fun resolveDecoration(underline: Int, lineThrough: Int): TextDecoration? {
    val decos = buildList {
        if (underline == 1) add(TextDecoration.Underline)
        if (lineThrough == 1) add(TextDecoration.LineThrough)
    }
    return if (decos.isEmpty()) null else TextDecoration.combine(decos)
}

// 1 = uppercase, 2 = lowercase, 3 = capitalize (first letter of each word).
private fun applyTransform(s: String, transform: Int): String = when (transform) {
    1 -> s.uppercase()
    2 -> s.lowercase()
    3 -> s.split(" ").joinToString(" ") { w -> w.replaceFirstChar { it.uppercaseChar() } }
    else -> s
}

private fun resolveTextAlign(align: Int): TextAlign {
    return when (align) {
        0 -> TextAlign.Start
        1 -> TextAlign.Center
        2 -> TextAlign.End
        else -> TextAlign.Start
    }
}