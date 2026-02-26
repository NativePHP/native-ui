package com.nativephp.plugins.compose_ui.ui

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode

object ToggleRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val initialValue = p.getBool("value")
        val onChangeCb = p.getCallbackId("on_change")
        val disabled = p.getBool("disabled")

        var checked by remember(node.id, initialValue) { mutableStateOf(initialValue) }

        Switch(
            checked = checked,
            onCheckedChange = { newValue ->
                checked = newValue
                if (onChangeCb != 0) {
                    NativeUIBridge.sendToggleChangeEvent(onChangeCb, node.id, newValue)
                }
            },
            modifier = modifier,
            enabled = !disabled
        )
    }
}
