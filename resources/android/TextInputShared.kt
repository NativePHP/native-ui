package com.nativephp.plugins.native_ui.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.nativephp.mobile.ui.MaterialIcon
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Shared plumbing for the text input variants. The two variants (outlined +
 * filled) only differ in which M3 primitive they construct and a few chrome
 * tweaks — extraction keeps prop parsing, keyboard mapping, dispatch policy,
 * and the decoration slot builders in one place.
 */

/** Dispatch policy — matches the `sync_mode` prop from the PHP side. */
internal enum class SyncMode { LIVE, BLUR, DEBOUNCE }

private fun parseSyncMode(value: String): SyncMode = when (value.lowercase()) {
    "blur"     -> SyncMode.BLUR
    "debounce" -> SyncMode.DEBOUNCE
    else       -> SyncMode.LIVE
}

/** Bag of parsed props — a plain data class, populated from the wire node. */
internal data class TextInputProps(
    val label: String,
    val placeholder: String,
    val supporting: String,
    val prefix: String,
    val suffix: String,
    val leadingIcon: String,
    val trailingIcon: String,
    val secure: Boolean,
    val multiline: Boolean,
    val maxLines: Int,
    val minLines: Int,
    val maxLength: Int,
    val keyboard: KeyboardType,
    val disabled: Boolean,
    val readOnly: Boolean,
    val isError: Boolean,
    val loading: Boolean,
    val size: String,
    val a11yLabel: String,
    val a11yHint: String,
    val serverValue: String,
    val onChangeCb: Int,
    val onSubmitCb: Int,
    val syncMode: SyncMode,
    val debounceMs: Int,
) {
    val enabled: Boolean get() = !disabled && !loading
    val visualTransformation: VisualTransformation
        get() = if (secure) PasswordVisualTransformation() else VisualTransformation.None
    val singleLine: Boolean get() = !multiline
}

internal fun parseTextInputProps(node: NativeUINode): TextInputProps {
    val p = node.props
    return TextInputProps(
        label        = p.getString("label"),
        placeholder  = p.getString("placeholder"),
        supporting   = p.getString("supporting"),
        prefix       = p.getString("prefix"),
        suffix       = p.getString("suffix"),
        leadingIcon  = p.getString("leading_icon"),
        trailingIcon = p.getString("trailing_icon"),
        secure       = p.getBool("secure"),
        multiline    = p.getBool("multiline"),
        maxLines     = p.getInt("max_lines").let { if (it > 0) it else if (p.getBool("multiline")) 5 else 1 },
        minLines     = p.getInt("min_lines").let { if (it > 0) it else 1 },
        maxLength    = p.getInt("max_length"),
        keyboard     = resolveKeyboardType(p.getString("keyboard")),
        disabled     = p.getBool("disabled"),
        readOnly     = p.getBool("read_only"),
        isError      = p.getBool("is_error"),
        loading      = p.getBool("loading"),
        size         = p.getString("size", "md"),
        a11yLabel    = p.getString("a11y_label"),
        a11yHint     = p.getString("a11y_hint"),
        serverValue  = p.getString("value"),
        onChangeCb   = p.getCallbackId("on_change"),
        onSubmitCb   = p.getCallbackId("on_submit"),
        syncMode     = parseSyncMode(p.getString("sync_mode", "live")),
        debounceMs   = p.getInt("debounce_ms").let { if (it > 0) it else 300 },
    )
}

/** String hint → M3 KeyboardType. Unknown falls through to text. */
internal fun resolveKeyboardType(kind: String): KeyboardType = when (kind.lowercase()) {
    "number"         -> KeyboardType.Number
    "email"          -> KeyboardType.Email
    "phone"          -> KeyboardType.Phone
    "url"            -> KeyboardType.Uri
    "decimal"        -> KeyboardType.Decimal
    "password"       -> KeyboardType.Password
    "numberpassword" -> KeyboardType.NumberPassword
    else             -> KeyboardType.Text
}

internal fun keyboardOptionsFor(props: TextInputProps): KeyboardOptions =
    KeyboardOptions(keyboardType = props.keyboard)

/**
 * Outbound dispatch state machine. Call [onTextChanged] whenever local text
 * updates; the policy decides whether to forward the change over the bridge
 * now, later (debounce), or on blur. [onBlur] flushes any pending value.
 */
internal class TextInputDispatcher(
    private val scope: CoroutineScope,
    private val props: TextInputProps,
    private val nodeId: Int,
    private val setLastSent: (String) -> Unit,
    private val getLastSent: () -> String,
) {
    private var debounceJob: Job? = null

    fun onTextChanged(value: String) {
        when (props.syncMode) {
            SyncMode.BLUR -> {
                // Don't dispatch mid-typing. `lastSentValue` stays anchored to
                // the last committed value so echo-prevention still blocks
                // same-value echoes of the committed state.
            }

            SyncMode.DEBOUNCE -> {
                // Cancel any in-flight timer; schedule a fresh one. First
                // keystroke gets a fresh N ms budget; each keystroke resets.
                debounceJob?.cancel()
                val captured = value
                val delayMs = props.debounceMs.coerceAtLeast(50).toLong()
                debounceJob = scope.launch {
                    delay(delayMs)
                    commit(captured)
                }
            }

            SyncMode.LIVE -> commit(value)
        }
    }

    fun onBlur(currentText: String) {
        // Flush pending change — covers both blur mode (deferred dispatch) and
        // debounce (in-flight timer that should resolve immediately on focus
        // loss instead of racing with keyboard dismiss).
        debounceJob?.cancel()
        debounceJob = null
        if (currentText != getLastSent()) {
            commit(currentText)
        }
    }

    fun onSubmit(currentText: String) {
        onBlur(currentText)
        if (props.onSubmitCb != 0) {
            NativeUIBridge.sendSubmitEvent(props.onSubmitCb, nodeId, currentText)
        }
    }

    private fun commit(value: String) {
        setLastSent(value)
        if (props.onChangeCb != 0) {
            NativeUIBridge.sendTextChangeEvent(props.onChangeCb, nodeId, value)
        }
    }
}

/** Common slot renderers — shared between OutlinedTextField and TextField. */
@Composable
internal fun labelSlot(text: String): (@Composable () -> Unit)? =
    if (text.isEmpty()) null else ({ Text(text) })

@Composable
internal fun placeholderSlot(text: String): (@Composable () -> Unit)? =
    if (text.isEmpty()) null else ({ Text(text) })

@Composable
internal fun supportingSlot(text: String): (@Composable () -> Unit)? =
    if (text.isEmpty()) null else ({ Text(text) })

@Composable
internal fun prefixSlot(text: String): (@Composable () -> Unit)? =
    if (text.isEmpty()) null else ({ Text(text) })

@Composable
internal fun suffixSlot(text: String): (@Composable () -> Unit)? =
    if (text.isEmpty()) null else ({ Text(text) })

@Composable
internal fun leadingIconSlot(name: String): (@Composable () -> Unit)? =
    if (name.isEmpty()) null else ({ MaterialIcon(name = name, contentDescription = null) })

@Composable
internal fun trailingIconSlot(name: String): (@Composable () -> Unit)? =
    if (name.isEmpty()) null else ({ MaterialIcon(name = name, contentDescription = null) })

/** Apply optional a11y label/hint to a modifier. */
internal fun Modifier.withA11y(label: String, hint: String): Modifier {
    var m = this
    if (label.isNotEmpty()) m = m.semantics { contentDescription = label }
    if (hint.isNotEmpty())  m = m.semantics { stateDescription   = hint }
    return m
}
