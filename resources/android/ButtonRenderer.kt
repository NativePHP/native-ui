package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode

object ButtonRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val label = p.getString("label")
        val pressCbId = p.getCallbackId("on_press").let { if (it != 0) it else node.onPress }
        val longPressCbId = node.onLongPress
        val disabled = p.getBool("disabled")
        val color = p.getColor("color", 0xFF6200EE.toInt())
        val labelColor = p.getColor("label_color", 0xFFFFFFFF.toInt())
        val fontSize = p.getFloat("font_size", 0f)

        if (longPressCbId != 0) {
            Box(
                modifier = modifier
                    .defaultMinSize(minWidth = 58.dp, minHeight = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(color))
                    .pointerInput(pressCbId, longPressCbId) {
                        detectTapGestures(
                            onTap = {
                                if (pressCbId != 0) {
                                    NativeUIBridge.sendPressEvent(pressCbId, node.id)
                                }
                            },
                            onLongPress = {
                                NativeUIBridge.sendLongPressEvent(longPressCbId, node.id)
                            }
                        )
                    }
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = Color(labelColor),
                    fontWeight = FontWeight.Medium,
                    fontSize = if (fontSize > 0f) fontSize.sp else 14.sp,
                    letterSpacing = 0.1.sp
                )
            }
        } else {
            Button(
                onClick = {
                    if (pressCbId != 0) {
                        NativeUIBridge.sendPressEvent(pressCbId, node.id)
                    }
                },
                modifier = modifier,
                enabled = !disabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(color),
                    contentColor = Color(labelColor)
                )
            ) {
                Text(
                    text = label,
                    fontSize = if (fontSize > 0f) fontSize.sp else TextUnit.Unspecified
                )
            }
        }
    }
}