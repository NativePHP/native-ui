package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativephp.mobile.ui.MaterialIcon
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.NodeView

object TimelineRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val horizontal = node.props.getString("orientation", "vertical") == "horizontal"

        if (horizontal) {
            Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                node.children.forEachIndexed { index, child ->
                    timelineChild(child, index == node.children.lastIndex, true)
                }
            }
        } else {
            Column(modifier = modifier) {
                node.children.forEachIndexed { index, child ->
                    timelineChild(child, index == node.children.lastIndex, false)
                }
            }
        }
    }
}

object TimelineBlockRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        Box(modifier = modifier) {
            TimelineBlockBody(node = node, isLast = true, horizontal = false)
        }
    }
}

@Composable
private fun timelineChild(node: NativeUINode, isLast: Boolean, horizontal: Boolean) {
    if (node.type == "timeline_block") {
        TimelineBlockBody(node = node, isLast = isLast, horizontal = horizontal)
    } else {
        NodeView(node = node)
    }
}

@Composable
private fun TimelineBlockBody(node: NativeUINode, isLast: Boolean, horizontal: Boolean) {
    if (horizontal) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimelineMarker(node)
                if (!isLast) {
                    Spacer(Modifier.width(64.dp).height(2.dp).background(Color(0xFFE5E7EB)))
                }
            }
            TimelineContent(node)
        }
    } else {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TimelineMarker(node)
                if (!isLast) {
                    Spacer(Modifier.width(2.dp).defaultMinSize(minHeight = 72.dp).background(Color(0xFFE5E7EB)))
                }
            }
            TimelineContent(
                node = node,
                modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 24.dp),
            )
        }
    }
}

@Composable
private fun TimelineMarker(node: NativeUINode) {
    val icon = node.props.getString("icon", "circle.fill")

    Box(
        modifier = Modifier
            .size(28.dp)
            .background(Color(0xFFF1F5F9), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        MaterialIcon(
            name = icon,
            contentDescription = icon,
            size = if (icon == "circle.fill") 9.dp else 15.dp,
            tint = Color(0xFF64748B),
        )
    }
}

@Composable
private fun TimelineContent(node: NativeUINode, modifier: Modifier = Modifier) {
    val heading = node.props.getString("heading")
    val status = node.props.getString("status")

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (heading.isNotEmpty() || status.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (heading.isNotEmpty()) {
                    Text(
                        text = heading,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                if (status.isNotEmpty()) {
                    Text(
                        text = status,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                    )
                }
            }
        }

        node.children.forEach { child ->
            NodeView(node = child)
        }
    }
}
