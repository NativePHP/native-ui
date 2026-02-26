package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
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

object TabRowRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val initialIndex = p.getInt("selected_index")
        val onChangeCb = p.getCallbackId("on_change")

        var selectedIndex by remember(node.id, initialIndex) { mutableStateOf(initialIndex) }

        val tabs = node.children.filter { it.type == "tab" }
        if (tabs.isEmpty()) return

        Column(modifier = modifier) {
            PrimaryTabRow(
                selectedTabIndex = selectedIndex.coerceIn(0, tabs.size - 1)
            ) {
                tabs.forEachIndexed { index, tabNode ->
                    val tabLabel = tabNode.props.getString("label")
                    val tabIcon = tabNode.props.getString("icon")
                    Tab(
                        selected = index == selectedIndex,
                        onClick = {
                            selectedIndex = index
                            if (onChangeCb != 0) {
                                NativeUIBridge.sendTabChangeEvent(onChangeCb, node.id, index)
                            }
                        },
                        text = if (tabLabel.isNotEmpty()) {
                            { Text(text = tabLabel) }
                        } else null,
                        icon = if (tabIcon.isNotEmpty()) {
                            {
                                com.nativephp.mobile.ui.MaterialIcon(
                                    name = tabIcon,
                                    contentDescription = tabIcon,
                                    size = 24.dp,
                                    tint = Color.Unspecified
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}
