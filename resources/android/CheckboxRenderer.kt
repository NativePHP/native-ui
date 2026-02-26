package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
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

object CheckboxRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val initialValue = p.getBool("value")
        val label = p.getString("label")
        val labelColor = p.getColor("label_color", 0xFF000000.toInt())
        val onChangeCb = p.getCallbackId("on_change")
        val disabled = p.getBool("disabled")

        var checked by remember(node.id, initialValue) { mutableStateOf(initialValue) }

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { newValue ->
                    checked = newValue
                    if (onChangeCb != 0) {
                        NativeUIBridge.sendCheckboxChangeEvent(onChangeCb, node.id, newValue)
                    }
                },
                enabled = !disabled
            )
            if (label.isNotEmpty()) {
                Text(
                    text = label,
                    color = Color(labelColor)
                )
            }
        }
    }
}
