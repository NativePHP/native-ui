package com.nativephp.plugins.compose_ui.ui

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode

object ButtonGroupRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val options = p.getStringList("options")
        val initialIndex = p.getInt("selected_index")
        val onChangeCb = p.getCallbackId("on_change")
        val disabled = p.getBool("disabled")

        var selectedIndex by remember(node.id, initialIndex) { mutableStateOf(initialIndex) }

        if (options.isEmpty()) return

        SingleChoiceSegmentedButtonRow(modifier = modifier) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = {
                        selectedIndex = index
                        if (onChangeCb != 0) {
                            NativeUIBridge.sendTabChangeEvent(onChangeCb, node.id, index)
                        }
                    },
                    selected = index == selectedIndex,
                    enabled = !disabled
                ) {
                    Text(label)
                }
            }
        }
    }
}
