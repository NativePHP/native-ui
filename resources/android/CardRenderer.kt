package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.RenderNode
import com.nativephp.plugins.native_ui.NativeUITheme

/**
 * Material3 Card — three variants (filled / outlined / elevated) mapped to
 * the M3 primitives [Card] / [OutlinedCard] / [ElevatedCard]. Colors drawn
 * from theme; no per-instance overrides (Model 3).
 */
object CardRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val variant = p.getString("variant", "filled")
        val a11yLabel = p.getString("a11y_label")
        val a11yHint  = p.getString("a11y_hint")

        val theme = if (isSystemInDarkTheme()) NativeUITheme.dark else NativeUITheme.light

        val clickModifier = modifier
            .let { m -> if (node.onPress != 0) m.clickable { NativeUIBridge.sendPressEvent(node.onPress, node.id) } else m }
            .let { m -> if (a11yLabel.isNotEmpty()) m.semantics { contentDescription = a11yLabel } else m }
            .let { m -> if (a11yHint.isNotEmpty())  m.semantics { stateDescription   = a11yHint  } else m }

        val content: @Composable () -> Unit = {
            Column {
                node.children.forEach { RenderNode(it) }
            }
        }

        when (variant) {
            "outlined" -> OutlinedCard(
                modifier = clickModifier,
                colors = CardDefaults.outlinedCardColors(
                    containerColor = theme.surface,
                    contentColor = theme.onSurface,
                ),
                border = BorderStroke(1.dp, theme.outline),
                content = { content() },
            )
            "elevated" -> ElevatedCard(
                modifier = clickModifier,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = theme.surface,
                    contentColor = theme.onSurface,
                ),
                content = { content() },
            )
            // "filled" (default)
            else -> Card(
                modifier = clickModifier,
                colors = CardDefaults.cardColors(
                    containerColor = theme.surfaceVariant,
                    contentColor = theme.onSurface,
                ),
                content = { content() },
            )
        }
    }
}
