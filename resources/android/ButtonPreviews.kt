package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.GenericProps
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.plugins.native_ui.LocalNativeUITheme
import com.nativephp.plugins.native_ui.NativeUITokens

/**
 * Compose @Preview harnesses for [ButtonRenderer].
 *
 * Lets you iterate on Button visuals in Android Studio's preview pane
 * without going through a full PHP render cycle. Previews use the fallback
 * theme (matches the plugin's config defaults).
 */

private fun mockNode(props: Map<String, Any>): NativeUINode = NativeUINode(
    id = 1,
    type = "button",
    layout = null,
    style = null,
    props = GenericProps(props),
    onPress = 0,
    onLongPress = 0,
    children = emptyList(),
)

@Preview(name = "Primary · md", showBackground = true)
@Composable
private fun Preview_PrimaryMd() = ThemedPreview {
    ButtonRenderer.Render(
        node = mockNode(mapOf("variant" to "primary", "label" to "Save changes")),
        modifier = Modifier,
    )
}

@Preview(name = "Primary · sm", showBackground = true)
@Composable
private fun Preview_PrimarySm() = ThemedPreview {
    ButtonRenderer.Render(
        node = mockNode(mapOf("variant" to "primary", "size" to "sm", "label" to "Save")),
        modifier = Modifier,
    )
}

@Preview(name = "Primary · lg", showBackground = true)
@Composable
private fun Preview_PrimaryLg() = ThemedPreview {
    ButtonRenderer.Render(
        node = mockNode(mapOf("variant" to "primary", "size" to "lg", "label" to "Get started")),
        modifier = Modifier,
    )
}

@Preview(name = "Primary · with leading icon", showBackground = true)
@Composable
private fun Preview_PrimaryWithIcon() = ThemedPreview {
    ButtonRenderer.Render(
        node = mockNode(mapOf("variant" to "primary", "icon" to "add", "label" to "Add item")),
        modifier = Modifier,
    )
}

@Preview(name = "Secondary", showBackground = true)
@Composable
private fun Preview_Secondary() = ThemedPreview {
    ButtonRenderer.Render(
        node = mockNode(mapOf("variant" to "secondary", "label" to "Cancel")),
        modifier = Modifier,
    )
}

@Preview(name = "Destructive", showBackground = true)
@Composable
private fun Preview_Destructive() = ThemedPreview {
    ButtonRenderer.Render(
        node = mockNode(mapOf("variant" to "destructive", "icon" to "delete", "label" to "Delete")),
        modifier = Modifier,
    )
}

@Preview(name = "Ghost", showBackground = true)
@Composable
private fun Preview_Ghost() = ThemedPreview {
    ButtonRenderer.Render(
        node = mockNode(mapOf("variant" to "ghost", "label" to "Learn more")),
        modifier = Modifier,
    )
}

@Preview(name = "Disabled", showBackground = true)
@Composable
private fun Preview_Disabled() = ThemedPreview {
    ButtonRenderer.Render(
        node = mockNode(mapOf("variant" to "primary", "disabled" to true, "label" to "Unavailable")),
        modifier = Modifier,
    )
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun Preview_Loading() = ThemedPreview {
    ButtonRenderer.Render(
        node = mockNode(mapOf("variant" to "primary", "loading" to true, "label" to "Saving…")),
        modifier = Modifier,
    )
}

@Preview(name = "All variants", showBackground = true, heightDp = 400)
@Composable
private fun Preview_AllVariants() = ThemedPreview {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ButtonRenderer.Render(mockNode(mapOf("variant" to "primary",     "label" to "Primary")),     Modifier)
        ButtonRenderer.Render(mockNode(mapOf("variant" to "secondary",   "label" to "Secondary")),   Modifier)
        ButtonRenderer.Render(mockNode(mapOf("variant" to "destructive", "label" to "Destructive")), Modifier)
        ButtonRenderer.Render(mockNode(mapOf("variant" to "ghost",       "label" to "Ghost")),       Modifier)
    }
}

@Composable
private fun ThemedPreview(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalNativeUITheme provides NativeUITokens.fallback) {
        content()
    }
}