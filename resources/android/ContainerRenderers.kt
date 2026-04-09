package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.*

object ColumnRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        DefaultContainerNode(node, modifier)
    }
}

object RowRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        DefaultContainerNode(node, modifier)
    }
}

object StackRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        Box(modifier = modifier) {
            node.children.forEach { child ->
                // Stack children may need fill sizing
                var childMod: Modifier = Modifier
                val layout = child.layout
                if (layout != null) {
                    if (layout.widthMode == SizeMode.FILL) childMod = childMod.fillMaxWidth()
                    if (layout.heightMode == SizeMode.FILL) childMod = childMod.fillMaxHeight()
                    if (layout.widthMode == SizeMode.FIXED && layout.width > 0f) childMod = childMod.width(layout.width.dp)
                    if (layout.heightMode == SizeMode.FIXED && layout.height > 0f) childMod = childMod.height(layout.height.dp)
                }
                NodeView(node = child, overrideModifier = childMod)
            }
        }
    }
}

object PressableRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        DefaultContainerNode(node, modifier)
    }
}

object CanvasRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        DefaultContainerNode(node, modifier.clipToBounds())
    }
}

object ScrollViewRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val horizontal = node.props.getBool("horizontal")

        if (horizontal) {
            LazyRow(modifier = modifier) {
                items(node.children, key = { it.id }) { child ->
                    NodeView(node = child)
                }
            }
        } else {
            LazyColumn(modifier = modifier) {
                items(node.children, key = { it.id }) { child ->
                    NodeView(node = child)
                }
            }
        }
    }
}
