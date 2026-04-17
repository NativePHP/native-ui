package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nativephp.mobile.ui.nativerender.NativeElementBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.NodeView

object ModalRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val visible = node.props.getBool("visible")
        val dismissible = node.props.getBool("dismissible", true)
        val onDismissCb = node.props.getCallbackId("on_dismiss")
        val nodeId = node.id

        val showModal = remember { mutableStateOf(false) }

        LaunchedEffect(visible) {
            showModal.value = visible
        }

        if (showModal.value) {
            Dialog(
                onDismissRequest = {
                    if (dismissible) {
                        showModal.value = false
                        if (onDismissCb != 0) {
                            NativeElementBridge.sendPressEvent(onDismissCb, nodeId)
                        }
                    }
                },
                properties = DialogProperties(
                    dismissOnBackPress = dismissible,
                    dismissOnClickOutside = dismissible,
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    node.children.forEach { child ->
                        NodeView(node = child)
                    }
                }
            }
        }
    }
}
