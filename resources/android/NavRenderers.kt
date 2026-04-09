package com.nativephp.plugins.compose_ui.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativephp.mobile.R
import com.nativephp.mobile.ui.NativeUIState
import com.nativephp.mobile.ui.getIconName
import com.nativephp.mobile.ui.nativerender.NativeEdgeDrawerState
import com.nativephp.mobile.ui.nativerender.NativeElementBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode
import kotlinx.coroutines.launch

object TopBarRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val props = node.props
        val title = props.getString("title", "")
        val showNavIcon = props.getBool("show_navigation_icon", true)
        val isDark = isSystemInDarkTheme()
        val textColor = if (isDark) Color.White else Color.Black
        val iconFont = remember { FontFamily(Font(R.font.material_icons)) }

        Row(
            modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showNavIcon) {
                Text(
                    text = getIconName("menu"),
                    fontFamily = iconFont,
                    fontSize = 24.sp,
                    color = textColor,
                    modifier = Modifier.padding(end = 16.dp).clickable {
                        NativeUIState.drawerScope?.launch { NativeUIState.drawerState?.open() }
                    }
                )
            }
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.weight(1f))
            val actions = node.children.filter { it.type == "top_bar_action" }
            val context = LocalContext.current
            for (action in actions.take(3)) {
                val icon = action.props.getString("icon", "more_vert")
                val url = action.props.getString("url")
                Text(
                    text = getIconName(icon), fontFamily = iconFont, fontSize = 24.sp, color = textColor,
                    modifier = Modifier.padding(start = 8.dp).clickable {
                        if (url.isNotEmpty()) {
                            try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } catch (_: Exception) {}
                        } else if (action.onPress != 0) {
                            NativeElementBridge.sendPressEvent(action.onPress, action.id)
                        }
                    }
                )
            }
        }
    }
}

object BottomNavRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val items = node.children.filter { it.type == "bottom_nav_item" }
        if (items.isEmpty()) return
        val iconFont = remember { FontFamily(Font(R.font.material_icons)) }

        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            for (item in items) {
                val label = item.props.getString("label", "")
                val icon = item.props.getString("icon", "circle")
                val active = item.props.getBool("active")
                val activeColor = if (active) Color(0xFF1976D2) else Color(0xFF757575)
                Column(
                    modifier = Modifier.weight(1f).clickable { if (item.onPress != 0) NativeElementBridge.sendPressEvent(item.onPress, item.id) }.padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = getIconName(icon), fontFamily = iconFont, fontSize = 24.sp, color = activeColor, textAlign = TextAlign.Center)
                    if (label.isNotEmpty()) Text(text = label, fontSize = 12.sp, color = activeColor, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

object SideNavRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        NativeEdgeDrawerState.sideNavNode.value = node
    }
}

object EmptyRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {}
}
