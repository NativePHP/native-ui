package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.MaterialIcon
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
        // contentAlignment = Center to match SwiftUI's ZStack default. Without
        // this, Compose Box stacks children at TopStart (upper-left), which
        // diverges from the iOS renderer.
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
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
        if (node.props.getBool("has_menu")) {
            // `:menu` attached — render the pressable's content as the
            // tap target, with a DropdownMenu anchored to the same Box.
            // Tapping the content sets `expanded = true`; the @press
            // callback is shadowed (matches the locked-in spec — menu
            // wins). On platforms with Liquid Glass equivalents, the
            // theme's surface tokens give the menu its translucent look.
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = modifier.clickable { expanded = true }) {
                DefaultContainerNode(node, Modifier)
                ExpressiveMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    node.children
                        .filter { it.type == "top_bar_action" }
                        .forEach { item ->
                            renderAttachedMenuItem(item) { expanded = false }
                        }
                }
            }
        } else {
            DefaultContainerNode(node, modifier)
        }
    }
}

/**
 * M3-Expressive "vertical menu" container. The classic `DropdownMenu`
 * popup (positioning, dismiss, scrim, enter/exit motion) wrapped with the
 * expressive surface treatment: a generously rounded container on the
 * themed `surface` color with a soft shadow — instead of the classic 4dp
 * near-square menu. Shared by all three menu sites (pressable / button /
 * list-item) so they stay identical.
 * See https://m3.material.io/components/menus/specs.
 */
@Composable
internal fun ExpressiveMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 6.dp,
        content = content,
    )
}

/// Render one menu item attached to a Pressable / Button / ListItem
/// trailing slot via `:menu` / `:trailing-menu`. Mirrors the pattern in
/// `TopBarActionView` (which handles top-bar action dropdowns).
///
/// Expressive vertical-menu row: an inset, rounded row whose press
/// ripple is a rounded pill (the expressive selection shape) rather than
/// the classic full-bleed rectangle. Leading icon in `onSurfaceVariant`,
/// label in `onSurface` (`error` for destructive). A `divider()` renders
/// as a thin inset rule grouping sections.
@Composable
internal fun renderAttachedMenuItem(item: NativeUINode, onSelected: () -> Unit) {
    if (item.props.getBool("divider")) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        return
    }
    val itemLabel = item.props.getString("label", "")
    val itemIcon = item.props.getString("icon", "")
    val isDestructive = item.props.getBool("destructive")

    val labelColor = if (isDestructive) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.onSurface
    val iconColor = if (isDestructive) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Inset so the rounded press ripple has breathing room from the
            // container edges (the expressive pill look). Clip BEFORE
            // clickable so the ripple is bounded to the rounded shape.
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onSelected()
                if (item.onPress != 0) {
                    NativeUIBridge.sendPressEvent(item.onPress, item.id)
                }
            }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (itemIcon.isNotEmpty()) {
            MaterialIcon(
                name = itemIcon,
                contentDescription = itemLabel,
                size = 24.dp,
                tint = iconColor,
            )
        }
        Text(
            itemLabel,
            color = labelColor,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
    }
}

object CanvasRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        DefaultContainerNode(node, modifier.clipToBounds())
    }
}

/**
 * Self-scrolling grid backed by Compose `LazyVerticalGrid` /
 * `LazyHorizontalGrid`. Only the rows currently in (or about to enter)
 * the viewport are composed, so this scales to thousands of cells
 * without paying for them at first paint. Use in place of a manually
 * chunked row-of-row grid whenever the cell count is large enough to
 * matter.
 */
object LazyGridRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val columns = node.props.getInt("columns", default = 2).coerceAtLeast(1)
        val gap = node.props.getFloat("gap", default = 0f).dp
        val horizontal = node.props.getBool("horizontal")

        if (horizontal) {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(columns),
                horizontalArrangement = Arrangement.spacedBy(gap),
                verticalArrangement = Arrangement.spacedBy(gap),
                modifier = modifier,
            ) {
                items(node.children, key = { it.id }) { child ->
                    NodeView(node = child)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                horizontalArrangement = Arrangement.spacedBy(gap),
                verticalArrangement = Arrangement.spacedBy(gap),
                modifier = modifier,
            ) {
                items(node.children, key = { it.id }) { child ->
                    NodeView(node = child)
                }
            }
        }
    }
}

object ScrollViewRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val horizontal = node.props.getBool("horizontal")
        val keyboardController = LocalSoftwareKeyboardController.current
        val scrollModifier = modifier.pointerInput(Unit) {
            detectVerticalDragGestures(onDragStart = { keyboardController?.hide() }) { _, _ -> }
        }

        if (horizontal) {
            LazyRow(modifier = modifier) {
                items(node.children, key = { it.id }) { child ->
                    NodeView(node = child)
                }
            }
        } else {
            LazyColumn(modifier = scrollModifier) {
                items(node.children, key = { it.id }) { child ->
                    NodeView(node = child)
                }
            }
        }
    }
}
