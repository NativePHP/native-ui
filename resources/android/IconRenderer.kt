package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode

object IconRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val name = p.getString("name")

        com.nativephp.mobile.ui.MaterialIcon(
            name = name,
            contentDescription = name,
            modifier = modifier.then(applyClickModifier(node)),
            size = p.getFloat("size", 24f).dp,
            tint = Color(p.getColor("color", 0xFF000000.toInt()))
        )
    }

    private fun applyClickModifier(node: NativeUINode): Modifier {
        return if (node.onPress != 0) {
            Modifier.clickable {
                NativeUIBridge.sendPressEvent(node.onPress, node.id)
            }
        } else {
            Modifier
        }
    }
}
