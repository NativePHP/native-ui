package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.SizeMode

object SpacerRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val layout = node.layout
        if (layout != null && layout.widthMode == SizeMode.FIXED && layout.width > 0) {
            Spacer(modifier = modifier.width(layout.width.dp))
        } else if (layout != null && layout.heightMode == SizeMode.FIXED && layout.height > 0) {
            Spacer(modifier = modifier.height(layout.height.dp))
        } else {
            Spacer(modifier = modifier.fillMaxWidth().height(0.dp))
        }
    }
}