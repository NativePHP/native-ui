package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.MaterialIcon
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode
import androidx.compose.foundation.isSystemInDarkTheme
import com.nativephp.plugins.native_ui.NativeUITheme
import com.nativephp.plugins.native_ui.NativeUITokens

/**
 * Material3 Button renderer.
 *
 * Maps semantic `variant` prop to the matching M3 primitive:
 *   - primary     → [Button] (filled) with theme.primary / onPrimary
 *   - secondary   → [FilledTonalButton] with theme.secondary / onSecondary
 *   - destructive → [Button] with theme.error / onError
 *   - ghost       → [TextButton] with theme.primary as content color
 *
 * All colors come from [LocalNativeUITheme]. No per-instance color/radius/shadow
 * overrides are honored — that's intentional (plan doc Model 3). For full visual
 * control, use `<native:pressable>`.
 */
object ButtonRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val variant = p.getString("variant", "primary")
        val size = p.getString("size", "md")
        val label = p.getString("label")
        val disabled = p.getBool("disabled")
        val loading = p.getBool("loading")
        val icon = p.getString("leading_icon")
        val iconTrailing = p.getString("trailing_icon")
        val a11yLabel = p.getString("a11y_label")
        val a11yHint = p.getString("a11y_hint")
        val pressCb = p.getCallbackId("on_press").let { if (it != 0) it else node.onPress }

        // Read the active token set from the shared store. Using the singleton
        // rather than a CompositionLocal because nothing in the render tree
        // currently provides one — the store is backed by `mutableStateOf`, so
        // Compose recomposes automatically when PHP pushes a theme update.
        val theme = if (isSystemInDarkTheme()) NativeUITheme.dark else NativeUITheme.light
        val metrics = sizeMetrics(size, theme)

        val onClick = { if (pressCb != 0) NativeUIBridge.sendPressEvent(pressCb, node.id) }

        val buttonModifier = modifier
            .defaultMinSize(minHeight = metrics.minHeight)
            .let { m ->
                if (a11yLabel.isNotEmpty()) m.semantics { contentDescription = a11yLabel } else m
            }
            .let { m ->
                if (a11yHint.isNotEmpty()) m.semantics { stateDescription = a11yHint } else m
            }

        val content: @Composable () -> Unit = {
            ButtonContent(
                label = label,
                icon = icon,
                iconTrailing = iconTrailing,
                loading = loading,
                iconSize = metrics.iconSize,
                textSize = metrics.textSize,
            )
        }

        val enabled = !disabled && !loading

        when (variant) {
            "secondary" -> FilledTonalButton(
                onClick = onClick,
                enabled = enabled,
                modifier = buttonModifier,
                contentPadding = metrics.contentPadding,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = theme.secondary,
                    contentColor = theme.onSecondary,
                ),
                content = { content() },
            )

            "destructive" -> Button(
                onClick = onClick,
                enabled = enabled,
                modifier = buttonModifier,
                contentPadding = metrics.contentPadding,
                colors = ButtonDefaults.buttonColors(
                    containerColor = theme.destructive,
                    contentColor = theme.onDestructive,
                ),
                content = { content() },
            )

            "ghost" -> TextButton(
                onClick = onClick,
                enabled = enabled,
                modifier = buttonModifier,
                contentPadding = metrics.contentPadding,
                colors = ButtonDefaults.textButtonColors(contentColor = theme.primary),
                content = { content() },
            )

            // "primary" (default) and any unknown value fall through to filled primary.
            else -> Button(
                onClick = onClick,
                enabled = enabled,
                modifier = buttonModifier,
                contentPadding = metrics.contentPadding,
                colors = ButtonDefaults.buttonColors(
                    containerColor = theme.primary,
                    contentColor = theme.onPrimary,
                ),
                content = { content() },
            )
        }
    }

    // ─── Internals ───────────────────────────────────────────────────────────

    private data class SizeMetrics(
        val minHeight: Dp,
        val contentPadding: PaddingValues,
        val iconSize: Dp,
        val textSize: TextUnit,
    )

    private fun sizeMetrics(size: String, theme: NativeUITokens): SizeMetrics = when (size) {
        "sm" -> SizeMetrics(
            minHeight = 32.dp,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            iconSize = 16.dp,
            textSize = theme.fontSm,
        )
        "lg" -> SizeMetrics(
            minHeight = 48.dp,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            iconSize = 22.dp,
            textSize = theme.fontLg,
        )
        else -> SizeMetrics(
            minHeight = 40.dp,
            contentPadding = ButtonDefaults.ContentPadding,
            iconSize = 18.dp,
            textSize = theme.fontMd,
        )
    }

    @Composable
    private fun ButtonContent(
        label: String,
        icon: String,
        iconTrailing: String,
        loading: Boolean,
        iconSize: Dp,
        textSize: TextUnit,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(iconSize),
                    strokeWidth = 2.dp,
                    color = LocalContentColor.current,
                )
                if (label.isNotEmpty()) {
                    Text(text = label, fontSize = textSize, maxLines = 1, softWrap = false)
                }
                return@Row
            }

            if (icon.isNotEmpty()) {
                MaterialIcon(
                    name = icon,
                    contentDescription = null,
                    size = iconSize,
                    tint = LocalContentColor.current,
                )
            }
            if (label.isNotEmpty()) {
                // maxLines + softWrap: button labels are short single-line by
                // convention. Without these, Compose wraps mid-word when the
                // parent flex-wrap row squeezes the button below its natural
                // width, producing fragments like "Destruct\nive". With them,
                // the button claims its full content width and flex-wrap kicks
                // the whole button to the next row.
                Text(text = label, fontSize = textSize, maxLines = 1, softWrap = false)
            }
            if (iconTrailing.isNotEmpty()) {
                MaterialIcon(
                    name = iconTrailing,
                    contentDescription = null,
                    size = iconSize,
                    tint = LocalContentColor.current,
                )
            }
        }
    }
}