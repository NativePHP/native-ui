package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.mobile.ui.nativerender.RenderNode
import com.nativephp.mobile.ui.nativerender.buildModifier

object CarouselRenderer {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val variant = p.getString("variant").ifEmpty { "multi_browse" }
        val itemWidth = p.getFloat("item_width").let { if (it > 0f) it else 200f }
        val itemSpacing = p.getFloat("item_spacing").let { if (it > 0f) it else 8f }

        val state = rememberCarouselState { node.children.size }

        when (variant) {
            "uncontained" -> {
                HorizontalUncontainedCarousel(
                    state = state,
                    itemWidth = itemWidth.dp,
                    itemSpacing = itemSpacing.dp,
                    modifier = modifier
                ) { index ->
                    val child = node.children[index]
                    RenderNode(
                        child,
                        buildModifier(child).clip(MaterialTheme.shapes.extraLarge)
                    )
                }
            }
            else -> {
                HorizontalMultiBrowseCarousel(
                    state = state,
                    preferredItemWidth = itemWidth.dp,
                    itemSpacing = itemSpacing.dp,
                    modifier = modifier
                ) { index ->
                    val child = node.children[index]
                    RenderNode(
                        child,
                        buildModifier(child).clip(MaterialTheme.shapes.extraLarge)
                    )
                }
            }
        }
    }
}
