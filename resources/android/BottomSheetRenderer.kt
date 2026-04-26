package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.RenderNode
import com.nativephp.plugins.native_ui.NativeUITheme

/**
 * Material3 ModalBottomSheet. Visibility driven by `visible`; drag-down and
 * tap-outside both route to the `@dismiss` callback.
 *
 * Theme-sourced container + scrim (Model 3). No per-instance `background_color`.
 * `skipPartiallyExpanded` flips automatically based on whether "medium" is
 * among the allowed detents — needed because M3's single-flag detent model
 * doesn't map 1:1 to iOS's PresentationDetent set.
 */
object BottomSheetRenderer {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val visible = p.getBool("visible")
        val onDismissCb = p.getCallbackId("on_dismiss")
        val detentsStr = p.getString("detents", "medium,large")
        val a11yLabel = p.getString("a11y_label")

        if (!visible) return

        val theme = if (isSystemInDarkTheme()) NativeUITheme.dark else NativeUITheme.light

        val skipPartial = !detentsStr.contains("medium")
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartial)

        val sheetModifier = modifier
            .let { m -> if (a11yLabel.isNotEmpty()) m.semantics { contentDescription = a11yLabel } else m }

        ModalBottomSheet(
            onDismissRequest = {
                if (onDismissCb != 0) {
                    NativeUIBridge.sendSheetDismissEvent(onDismissCb, node.id)
                }
            },
            sheetState = sheetState,
            containerColor = theme.surface,
            contentColor = theme.onSurface,
            scrimColor = BottomSheetDefaults.ScrimColor,
        ) {
            Column(modifier = sheetModifier) {
                node.children.forEach { RenderNode(it) }
            }
        }
    }
}
