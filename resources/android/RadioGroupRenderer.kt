package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.RenderNode
import com.nativephp.mobile.ui.nativerender.buildModifier

object RadioGroupRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val initialValue = p.getString("value")
        val onChangeCb = p.getCallbackId("on_change")

        var selectedValue by remember(node.id, initialValue) { mutableStateOf(initialValue) }

        Column(modifier = modifier) {
            node.children.forEach { child ->
                if (child.type == "radio") {
                    RadioRenderer.Render(
                        node = child,
                        modifier = buildModifier(child),
                        selectedValue = selectedValue,
                        onSelect = { value ->
                            selectedValue = value
                            if (onChangeCb != 0) {
                                NativeUIBridge.sendRadioChangeEvent(onChangeCb, node.id, value)
                            }
                        }
                    )
                } else {
                    RenderNode(child)
                }
            }
        }
    }
}
