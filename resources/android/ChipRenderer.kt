package com.nativephp.plugins.compose_ui.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode

object ChipRenderer {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val label = p.getString("label")
        val initialSelected = p.getBool("selected")
        val onChangeCb = p.getCallbackId("on_change")
        val iconName = p.getString("icon")

        var isSelected by remember(node.id, initialSelected) { mutableStateOf(initialSelected) }

        FilterChip(
            selected = isSelected,
            onClick = {
                isSelected = !isSelected
                if (onChangeCb != 0) {
                    NativeUIBridge.sendToggleChangeEvent(onChangeCb, node.id, isSelected)
                }
            },
            label = { Text(label) },
            modifier = modifier,
            leadingIcon = if (iconName.isNotEmpty()) {
                {
                    com.nativephp.mobile.ui.MaterialIcon(
                        name = iconName,
                        contentDescription = iconName,
                        size = 18.dp,
                        tint = Color.Unspecified
                    )
                }
            } else null
        )
    }
}
