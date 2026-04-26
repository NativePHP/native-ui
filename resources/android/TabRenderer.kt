package com.nativephp.plugins.native_ui.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nativephp.mobile.ui.nativerender.NativeUINode

object TabRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        // Tabs are rendered by TabRowRenderer — this is a no-op placeholder
    }
}
