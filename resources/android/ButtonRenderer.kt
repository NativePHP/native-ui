package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.argbToComposeColor

object ButtonRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val label = p.getString("label")
        val pressCbId = p.getCallbackId("on_press").let { if (it != 0) it else node.onPress }
        val longPressCbId = node.onLongPress
        val disabled = p.getBool("disabled")
        val labelColor = p.getColor("label_color", 0xFFFFFFFF.toInt())
        val fontSize = p.getFloat("font_size", 0f)

        // Background, border radius, and shape come from nodeStyle via the modifier.
        // We just render centered text inside.
        Box(
            modifier = modifier
                .alpha(if (disabled) 0.5f else 1.0f)
                .then(
                    if (!disabled && (pressCbId != 0 || longPressCbId != 0)) {
                        Modifier.pointerInput(pressCbId, longPressCbId) {
                            detectTapGestures(
                                onTap = {
                                    if (pressCbId != 0) {
                                        NativeUIBridge.sendPressEvent(pressCbId, node.id)
                                    }
                                },
                                onLongPress = if (longPressCbId != 0) {
                                    { NativeUIBridge.sendLongPressEvent(longPressCbId, node.id) }
                                } else null
                            )
                        }
                    } else Modifier
                )
            ,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = argbToComposeColor(labelColor),
                fontWeight = FontWeight.Medium,
                fontSize = if (fontSize > 0f) fontSize.sp else TextUnit.Unspecified,
                textAlign = TextAlign.Center,
                letterSpacing = 0.1.sp
            )
        }
    }
}
