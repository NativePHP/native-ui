package com.nativephp.plugins.compose_ui.ui

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nativephp.mobile.ui.nativerender.NativeUINode

object DividerRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        HorizontalDivider(modifier = modifier)
    }
}