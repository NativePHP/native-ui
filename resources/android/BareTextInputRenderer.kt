package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.sp
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.plugins.native_ui.NativeUITheme

/**
 * Chromeless text input — Compose `BasicTextField` with no decoration.
 *
 * Counterpart to iOS's `NativeUIBareTextInputRenderer`. Use when the
 * surrounding container provides the visible chrome (chat input pill,
 * search bar, inline edit field, etc.).
 *
 * Colors come from [NativeUITheme]; no per-instance overrides.
 */
object BareTextInputRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val props = parseTextInputProps(node)
        val theme = if (isSystemInDarkTheme()) NativeUITheme.dark else NativeUITheme.light

        // Echo-prevention sync — same shape as the outlined variant.
        var text by remember { mutableStateOf(props.serverValue) }
        var lastSentValue by remember { mutableStateOf(props.serverValue) }

        LaunchedEffect(props.serverValue) {
            if (props.serverValue != lastSentValue) {
                text = props.serverValue
                lastSentValue = props.serverValue
            }
        }

        BasicTextField(
            value = text,
            onValueChange = { newText ->
                if (props.disabled || props.readOnly) return@BasicTextField
                text = newText
                lastSentValue = newText
                props.dispatchChange?.invoke(newText)
            },
            modifier = modifier,
            enabled = !props.disabled,
            readOnly = props.readOnly,
            textStyle = LocalTextStyle.current.copy(
                color = if (props.disabled) theme.onSurface.copy(alpha = 0.6f) else theme.onSurface,
                fontSize = props.textSize.sp
            ),
            cursorBrush = SolidColor(if (props.isError) theme.destructive else theme.primary),
            singleLine = !props.multiline,
            decorationBox = { innerTextField ->
                if (text.isEmpty() && props.placeholder.isNotEmpty()) {
                    Text(
                        text = props.placeholder,
                        color = theme.onSurfaceVariant,
                        fontSize = props.textSize.sp
                    )
                }
                innerTextField()
            },
            keyboardActions = KeyboardActions(onAny = { props.dispatchSubmit?.invoke(text) })
        )
    }
}
