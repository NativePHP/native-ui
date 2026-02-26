package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
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

        if (!visible) return

        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
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
