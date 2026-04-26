package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.plugins.native_ui.NativeUITheme

/**
 * Material3 Checkbox renderer.
 *
 * Echo-prevention (plan K). Theme-sourced colors — no per-instance overrides
 * (Model 3).
 */
object CheckboxRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val serverValue = p.getBool("value")
        val label       = p.getString("label")
        val onChangeCb  = p.getCallbackId("on_change")
        val disabled    = p.getBool("disabled")
        val a11yLabel   = p.getString("a11y_label")
        val a11yHint    = p.getString("a11y_hint")

        val theme = if (isSystemInDarkTheme()) NativeUITheme.dark else NativeUITheme.light

        var checked by remember(node.id) { mutableStateOf(serverValue) }
        var lastSentValue by remember(node.id) { mutableStateOf(serverValue) }

        LaunchedEffect(serverValue) {
            if (serverValue != lastSentValue) {
                checked = serverValue
                lastSentValue = serverValue
            }
        }

        val colors = CheckboxDefaults.colors(
            checkedColor = theme.primary,
            uncheckedColor = theme.outline,
            checkmarkColor = theme.onPrimary,
            disabledCheckedColor = theme.primary.copy(alpha = 0.38f),
            disabledUncheckedColor = theme.outline.copy(alpha = 0.38f),
        )

        val rowModifier = modifier
            .let { m -> if (a11yLabel.isNotEmpty()) m.semantics { contentDescription = a11yLabel } else m }
            .let { m -> if (a11yHint.isNotEmpty())  m.semantics { stateDescription   = a11yHint  } else m }

        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { new ->
                    checked = new
                    lastSentValue = new
                    if (onChangeCb != 0) {
                        NativeUIBridge.sendCheckboxChangeEvent(onChangeCb, node.id, new)
                    }
                },
                enabled = !disabled,
                colors = colors,
            )
            if (label.isNotEmpty()) {
                Text(text = label, color = theme.onSurface)
            }
        }
    }
}
