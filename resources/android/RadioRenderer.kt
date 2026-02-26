package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.NativeUINode

object RadioRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        // Standalone radio (outside RadioGroup) — no selection state
        Render(node, modifier, selectedValue = null, onSelect = null)
    }

    @Composable
    fun Render(
        node: NativeUINode,
        modifier: Modifier,
        selectedValue: String?,
        onSelect: ((String) -> Unit)?
    ) {
        val p = node.props
        val value = p.getString("value")
        val label = p.getString("label")
        val labelColor = p.getColor("label_color", 0xFF000000.toInt())
        val disabled = p.getBool("disabled")
        val isSelected = selectedValue == value

        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(enabled = !disabled) {
                    onSelect?.invoke(value)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = {
                    onSelect?.invoke(value)
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
