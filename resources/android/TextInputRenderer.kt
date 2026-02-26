package com.nativephp.plugins.compose_ui.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode

object TextInputRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val initialValue = p.getString("value")
        val placeholder = p.getString("placeholder")
        val label = p.getString("label")
        val onChangeCb = p.getCallbackId("on_change")
        val onSubmitCb = p.getCallbackId("on_submit")
        val secure = p.getBool("secure")
        val multiline = p.getBool("multiline")
        val variant = p.getInt("variant")
        val disabled = p.getBool("disabled")
        val readOnly = p.getBool("read_only")
        val isError = p.getBool("is_error")
        val maxLength = p.getInt("max_length")
        val maxLines = p.getInt("max_lines")
        val minLines = p.getInt("min_lines")
        val keyboardInt = p.getInt("keyboard")
        val prefix = p.getString("prefix")
        val suffix = p.getString("suffix")
        val supporting = p.getString("supporting")
        val leadingIcon = p.getString("leading_icon")
        val trailingIcon = p.getString("trailing_icon")
        val fontSize = p.getFloat("font_size")
        val fontWeightInt = p.getInt("font_weight")

        // Colors
        val textColorInt = p.getColor("text_color", 0)
        val accentColorInt = p.getColor("color", 0)
        val containerColorInt = p.getColor("container_color", 0)
        val labelColorInt = p.getColor("label_color", 0)
        val supportingColorInt = p.getColor("supporting_color", 0)

        var text by remember(node.id, initialValue) { mutableStateOf(initialValue) }

        val onValueChange: (String) -> Unit = { newValue ->
            val filtered = if (maxLength > 0) newValue.take(maxLength) else newValue
            text = filtered
            if (onChangeCb != 0) {
                NativeUIBridge.sendTextChangeEvent(onChangeCb, node.id, filtered)
            }
        }

        val singleLine = !multiline
        val resolvedMaxLines = when {
            singleLine -> 1
            maxLines > 0 -> maxLines
            else -> Int.MAX_VALUE
        }
        val resolvedMinLines = if (minLines > 0) minLines else 1

        val textStyle = buildTextStyle(fontSize, fontWeightInt, textColorInt)
        val visualTransformation = if (secure) PasswordVisualTransformation() else VisualTransformation.None
        val keyboardOptions = KeyboardOptions(
            keyboardType = resolveKeyboardType(keyboardInt),
            imeAction = if (onSubmitCb != 0) ImeAction.Done else ImeAction.Default,
            capitalization = if (keyboardInt == 0) KeyboardCapitalization.Sentences else KeyboardCapitalization.None
        )
        val keyboardActions = KeyboardActions(
            onDone = {
                if (onSubmitCb != 0) {
                    NativeUIBridge.sendSubmitEvent(onSubmitCb, node.id, text)
                }
            }
        )

        // Derive secondary colors from textColor when set (e.g. dark mode)
        val textColor = if (textColorInt != 0) Color(textColorInt) else Color.Unspecified
        val accentColor = if (accentColorInt != 0) Color(accentColorInt) else Color.Unspecified
        val containerColor = if (containerColorInt != 0) Color(containerColorInt) else Color.Unspecified
        val labelColor = if (labelColorInt != 0) Color(labelColorInt) else Color.Unspecified
        val supportingColor = if (supportingColorInt != 0) Color(supportingColorInt) else Color.Unspecified

        // When textColor is set, derive sensible defaults for the secondary colors
        val hasCustomColors = textColorInt != 0 || accentColorInt != 0 || containerColorInt != 0
        val derivedMuted = if (textColorInt != 0) Color(textColorInt).copy(alpha = 0.6f) else Color.Unspecified
        val derivedSubtle = if (textColorInt != 0) Color(textColorInt).copy(alpha = 0.4f) else Color.Unspecified

        val resolvedLabelColor = when {
            labelColor != Color.Unspecified -> labelColor
            else -> derivedMuted
        }
        val resolvedSupportingColor = when {
            supportingColor != Color.Unspecified -> supportingColor
            else -> derivedMuted
        }
        val resolvedIconTint = if (textColorInt != 0) Color(textColorInt).copy(alpha = 0.7f) else Color.Unspecified

        val labelContent: @Composable (() -> Unit)? = if (label.isNotEmpty()) {
            { Text(label) }
        } else null

        val placeholderContent: @Composable (() -> Unit)? = if (placeholder.isNotEmpty()) {
            { Text(placeholder) }
        } else null

        val prefixContent: @Composable (() -> Unit)? = if (prefix.isNotEmpty()) {
            { Text(prefix) }
        } else null

        val suffixContent: @Composable (() -> Unit)? = if (suffix.isNotEmpty()) {
            { Text(suffix) }
        } else null

        val supportingContent: @Composable (() -> Unit)? = if (supporting.isNotEmpty()) {
            { Text(supporting) }
        } else null

        val leadingIconContent: @Composable (() -> Unit)? = if (leadingIcon.isNotEmpty()) {
            {
                com.nativephp.mobile.ui.MaterialIcon(
                    name = leadingIcon,
                    contentDescription = leadingIcon,
                    size = 24.dp,
                    tint = resolvedIconTint
                )
            }
        } else null

        val trailingIconContent: @Composable (() -> Unit)? = if (trailingIcon.isNotEmpty()) {
            {
                com.nativephp.mobile.ui.MaterialIcon(
                    name = trailingIcon,
                    contentDescription = trailingIcon,
                    size = 24.dp,
                    tint = resolvedIconTint
                )
            }
        } else null

        when (variant) {
            1 -> {
                val colors = if (hasCustomColors) TextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = containerColor,
                    cursorColor = if (accentColor != Color.Unspecified) accentColor else Color.Unspecified,
                    focusedIndicatorColor = if (accentColor != Color.Unspecified) accentColor else Color.Unspecified,
                    unfocusedIndicatorColor = derivedSubtle,
                    focusedLabelColor = if (accentColor != Color.Unspecified) accentColor else Color.Unspecified,
                    unfocusedLabelColor = resolvedLabelColor,
                    focusedPlaceholderColor = derivedMuted,
                    unfocusedPlaceholderColor = derivedSubtle,
                    focusedSupportingTextColor = resolvedSupportingColor,
                    unfocusedSupportingTextColor = resolvedSupportingColor,
                    focusedPrefixColor = derivedMuted,
                    unfocusedPrefixColor = derivedMuted,
                    focusedSuffixColor = derivedMuted,
                    unfocusedSuffixColor = derivedMuted
                ) else TextFieldDefaults.colors()

                TextField(
                    value = text,
                    onValueChange = onValueChange,
                    modifier = modifier,
                    enabled = !disabled,
                    readOnly = readOnly,
                    textStyle = textStyle,
                    label = labelContent,
                    placeholder = placeholderContent,
                    leadingIcon = leadingIconContent,
                    trailingIcon = trailingIconContent,
                    prefix = prefixContent,
                    suffix = suffixContent,
                    supportingText = supportingContent,
                    isError = isError,
                    visualTransformation = visualTransformation,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    singleLine = singleLine,
                    maxLines = resolvedMaxLines,
                    minLines = resolvedMinLines,
                    colors = colors
                )
            }
            else -> {
                val colors = if (hasCustomColors) OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = containerColor,
                    cursorColor = if (accentColor != Color.Unspecified) accentColor else Color.Unspecified,
                    focusedBorderColor = if (accentColor != Color.Unspecified) accentColor else Color.Unspecified,
                    unfocusedBorderColor = derivedSubtle,
                    focusedLabelColor = if (accentColor != Color.Unspecified) accentColor else Color.Unspecified,
                    unfocusedLabelColor = resolvedLabelColor,
                    focusedPlaceholderColor = derivedMuted,
                    unfocusedPlaceholderColor = derivedSubtle,
                    focusedSupportingTextColor = resolvedSupportingColor,
                    unfocusedSupportingTextColor = resolvedSupportingColor,
                    focusedPrefixColor = derivedMuted,
                    unfocusedPrefixColor = derivedMuted,
                    focusedSuffixColor = derivedMuted,
                    unfocusedSuffixColor = derivedMuted
                ) else OutlinedTextFieldDefaults.colors()

                OutlinedTextField(
                    value = text,
                    onValueChange = onValueChange,
                    modifier = modifier,
                    enabled = !disabled,
                    readOnly = readOnly,
                    textStyle = textStyle,
                    label = labelContent,
                    placeholder = placeholderContent,
                    leadingIcon = leadingIconContent,
                    trailingIcon = trailingIconContent,
                    prefix = prefixContent,
                    suffix = suffixContent,
                    supportingText = supportingContent,
                    isError = isError,
                    visualTransformation = visualTransformation,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    singleLine = singleLine,
                    maxLines = resolvedMaxLines,
                    minLines = resolvedMinLines,
                    colors = colors
                )
            }
        }
    }

    private fun buildTextStyle(fontSize: Float, fontWeightInt: Int, textColor: Int): TextStyle {
        return TextStyle(
            fontSize = if (fontSize > 0f) fontSize.sp else TextStyle.Default.fontSize,
            fontWeight = resolveFontWeight(fontWeightInt),
            color = if (textColor != 0) Color(textColor) else Color.Unspecified
        )
    }

    private fun resolveFontWeight(weight: Int): FontWeight {
        return when (weight) {
            1 -> FontWeight.Thin
            2 -> FontWeight.Light
            3 -> FontWeight.Normal
            4 -> FontWeight.Medium
            5 -> FontWeight.SemiBold
            6 -> FontWeight.Bold
            7 -> FontWeight.ExtraBold
            else -> FontWeight.Normal
        }
    }

    private fun resolveKeyboardType(type: Int): KeyboardType {
        return when (type) {
            1 -> KeyboardType.Number
            2 -> KeyboardType.Email
            3 -> KeyboardType.Phone
            4 -> KeyboardType.Uri
            5 -> KeyboardType.Decimal
            6 -> KeyboardType.NumberPassword
            else -> KeyboardType.Text
        }
    }
}
