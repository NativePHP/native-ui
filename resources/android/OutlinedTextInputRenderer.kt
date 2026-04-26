package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.nativephp.mobile.ui.nativerender.NativeUINode
import com.nativephp.plugins.native_ui.NativeUITheme

/**
 * Material3 outlined text field.
 *
 * Emphasis: lower than filled. Border-only chrome, good default for forms.
 *
 * All colors drawn from [NativeUITheme] — per-instance color overrides are
 * intentionally not honored (plan doc Model 3).
 */
object OutlinedTextInputRenderer {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val props = parseTextInputProps(node)
        val theme = if (isSystemInDarkTheme()) NativeUITheme.dark else NativeUITheme.light
        val scope = rememberCoroutineScope()

        // Echo-prevention sync (plan K). Local state owns what the user is
        // typing. PHP may push an updated `value` prop at any time; we only
        // accept it if it diverges from `lastSentValue` — otherwise it's just
        // the Livewire echo of our own change and would clobber the caret.
        var text by remember { mutableStateOf(props.serverValue) }
        var lastSentValue by remember { mutableStateOf(props.serverValue) }

        LaunchedEffect(props.serverValue) {
            if (props.serverValue != lastSentValue) {
                text = props.serverValue
                lastSentValue = props.serverValue
            }
        }

        // Sync-mode dispatcher (plan L). Owns the live / blur / debounce
        // decision for outbound change events.
        val dispatcher = remember(props.syncMode, props.debounceMs, props.onChangeCb) {
            TextInputDispatcher(
                scope = scope,
                props = props,
                nodeId = node.id,
                setLastSent = { lastSentValue = it },
                getLastSent = { lastSentValue },
            )
        }

        // Observe focus via the field's InteractionSource; we use that edge
        // (focused → unfocused) to flush pending changes in blur / debounce
        // modes. Passing our own source also means we don't pay for M3's
        // default ripple-focus-hover machinery elsewhere.
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            val focusStack = mutableListOf<FocusInteraction.Focus>()
            interactionSource.interactions.collect { interaction: Interaction ->
                when (interaction) {
                    is FocusInteraction.Focus   -> focusStack += interaction
                    is FocusInteraction.Unfocus -> {
                        focusStack.remove(interaction.focus)
                        if (focusStack.isEmpty()) dispatcher.onBlur(text)
                    }
                    else -> { /* ignore press/hover/drag */ }
                }
            }
        }

        val textSize = when (props.size) {
            "sm" -> theme.fontSm
            "lg" -> theme.fontLg
            else -> theme.fontMd
        }

        OutlinedTextField(
            value = text,
            onValueChange = { new ->
                val filtered = if (props.maxLength > 0) new.take(props.maxLength) else new
                text = filtered
                dispatcher.onTextChanged(filtered)
            },
            modifier = modifier.withA11y(props.a11yLabel, props.a11yHint),
            enabled = props.enabled,
            readOnly = props.readOnly,
            interactionSource = interactionSource,
            label = labelSlot(props.label),
            placeholder = placeholderSlot(props.placeholder),
            supportingText = supportingSlot(props.supporting),
            prefix = prefixSlot(props.prefix),
            suffix = suffixSlot(props.suffix),
            leadingIcon = leadingIconSlot(props.leadingIcon),
            trailingIcon = if (props.loading) {
                { CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = theme.onSurfaceVariant) }
            } else trailingIconSlot(props.trailingIcon),
            isError = props.isError,
            singleLine = props.singleLine,
            maxLines = props.maxLines,
            minLines = props.minLines,
            visualTransformation = props.visualTransformation,
            keyboardOptions = keyboardOptionsFor(props),
            keyboardActions = KeyboardActions(onDone = { dispatcher.onSubmit(text) }),
            textStyle = TextStyle(fontSize = textSize, color = theme.onSurface),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = theme.onSurface,
                unfocusedTextColor = theme.onSurface,
                disabledTextColor = theme.onSurface.copy(alpha = 0.6f),
                errorTextColor = theme.onSurface,
                cursorColor = theme.primary,
                errorCursorColor = theme.destructive,
                focusedBorderColor = theme.primary,
                unfocusedBorderColor = theme.outline,
                disabledBorderColor = theme.outline.copy(alpha = 0.5f),
                errorBorderColor = theme.destructive,
                focusedLabelColor = theme.primary,
                unfocusedLabelColor = theme.onSurfaceVariant,
                disabledLabelColor = theme.onSurfaceVariant.copy(alpha = 0.6f),
                errorLabelColor = theme.destructive,
                focusedPlaceholderColor = theme.onSurfaceVariant,
                unfocusedPlaceholderColor = theme.onSurfaceVariant,
                focusedSupportingTextColor = theme.onSurfaceVariant,
                unfocusedSupportingTextColor = theme.onSurfaceVariant,
                errorSupportingTextColor = theme.destructive,
                focusedLeadingIconColor = theme.onSurfaceVariant,
                unfocusedLeadingIconColor = theme.onSurfaceVariant,
                focusedTrailingIconColor = theme.onSurfaceVariant,
                unfocusedTrailingIconColor = theme.onSurfaceVariant,
            ),
        )
    }
}
