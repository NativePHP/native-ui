package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.RenderNode
import com.nativephp.plugins.native_ui.NativeUITheme

/**
 * Themed screen backdrop.
 *
 * Applies `theme.background` as the full-bleed background and sets the
 * default content color to `theme.onBackground` for descendants via
 * [LocalContentColor]. Use as the root of a page.
 *
 * Dark mode is handled automatically.
 */
object ScreenRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val theme = if (isSystemInDarkTheme()) NativeUITheme.dark else NativeUITheme.light

        CompositionLocalProvider(LocalContentColor provides theme.onBackground) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(theme.background),
            ) {
                node.children.forEach { child -> RenderNode(child) }
            }
        }
    }
}