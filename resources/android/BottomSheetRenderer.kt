package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import com.nativephp.mobile.ui.nativerender.argbToComposeColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.RenderNode

object BottomSheetRenderer {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val visible = p.getBool("visible")
        val onDismissCb = p.getCallbackId("on_dismiss")
        val detentsStr = p.getString("detents", "medium,large")
        val bgColorArgb = p.getColor("background_color", 0)

        if (!visible) return

        // skipPartiallyExpanded = true means sheet opens fully (like "large" only)
        // false means it starts at half height (medium) and can expand
        val skipPartial = !detentsStr.contains("medium")
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartial)

        val containerColor = if (bgColorArgb != 0) argbToComposeColor(bgColorArgb) else Color.Unspecified

        ModalBottomSheet(
            containerColor = if (containerColor != Color.Unspecified) containerColor else androidx.compose.material3.BottomSheetDefaults.ContainerColor,
            onDismissRequest = {
                if (onDismissCb != 0) {
                    NativeUIBridge.sendSheetDismissEvent(onDismissCb, node.id)
                }
            },
            sheetState = sheetState
        ) {
            Column(modifier = modifier) {
                node.children.forEach { RenderNode(it) }
            }
        }
    }
}
