package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.argbToComposeColor

object ToggleRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val label = p.getString("label")
        val initialValue = p.getBool("value")
        val onChangeCb = p.getCallbackId("on_change")
        val disabled = p.getBool("disabled")

        var checked by remember(node.id, initialValue) { mutableStateOf(initialValue) }

        // Tint from style bgColor
        val tintArgb = node.style?.bgColor ?: 0
        val tintAlpha = (tintArgb.toLong() and 0xFF000000L) ushr 24
        val tintColor: Color? = if (tintArgb != 0 && tintAlpha != 0L) argbToComposeColor(tintArgb) else null

        val colors = if (tintColor != null) {
            SwitchDefaults.colors(
                checkedThumbColor = tintColor,
                checkedTrackColor = tintColor.copy(alpha = 0.5f)
            )
        } else SwitchDefaults.colors()

        val onChanged = { newValue: Boolean ->
            checked = newValue
            if (onChangeCb != 0) {
                NativeUIBridge.sendToggleChangeEvent(onChangeCb, node.id, newValue)
            }
        }

        if (label.isNotEmpty()) {
            val isDark = isSystemInDarkTheme()
            val darkColor = if (isDark) p.getColor("dark_color", 0) else 0
            val labelArgb = p.getColor("label_color", 0)
            val colorArgb = p.getColor("color", 0)
            val textColor = when {
                darkColor != 0 -> argbToComposeColor(darkColor)
                labelArgb != 0 -> argbToComposeColor(labelArgb)
                colorArgb != 0 -> argbToComposeColor(colorArgb)
                else -> Color.Unspecified
            }

            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                Text(text = label, modifier = Modifier.weight(1f), color = textColor)
                Spacer(modifier = Modifier.width(8.dp))
                Switch(checked = checked, onCheckedChange = onChanged, enabled = !disabled, colors = colors)
            }
        } else {
            Switch(checked = checked, onCheckedChange = onChanged, modifier = modifier, enabled = !disabled, colors = colors)
        }
    }
}
