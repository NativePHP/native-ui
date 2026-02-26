package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativephp.mobile.ui.nativerender.NativeUINode

object BadgeRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val count = p.getInt("count")
        val color = p.getColor("color", 0xFFFF0000.toInt())
        val textColor = p.getColor("text_color", 0xFFFFFFFF.toInt())

        Box(
            modifier = modifier
                .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(color))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                color = Color(textColor),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
