package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.RenderNode

object CardRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val variant = node.props.getInt("variant")

        val clickModifier = if (node.onPress != 0) {
            modifier.clickable { NativeUIBridge.sendPressEvent(node.onPress, node.id) }
        } else {
            modifier
        }

        val content: @Composable () -> Unit = {
            Column {
                node.children.forEach { RenderNode(it) }
            }
        }

        when (variant) {
            1 -> OutlinedCard(
                modifier = clickModifier,
                content = { content() }
            )
            2 -> ElevatedCard(
                modifier = clickModifier,
                content = { content() }
            )
            else -> Card(
                modifier = clickModifier,
                content = { content() }
            )
        }
    }
}
