import SwiftUI
import UIKit

/// Shared inner TextField core for both `outlined-text-input` and
/// `filled-text-input` variants. Handles:
///   - value binding with echo-prevention sync (PHP can update `value` at any
///     time; we avoid clobbering in-flight local edits by tracking the last
///     value we sent out)
///   - `sync_mode` dispatch policy (live | debounce | blur) — controlled by
///     the `native:model` directive modifier chain
///   - secure / multiline input
///   - keyboard type, submit label
///   - disabled / readOnly state
///   - onChange / onSubmit callbacks
///
/// Variant-specific chrome (label, icons, border/fill, supporting text) lives
/// in the variant renderers that wrap this view.
struct NativeUITextInputCore: View {
    let node: NativeUINode
    let textSize: CGFloat
    let contentColor: Color
    let tintColor: Color

    @State private var text: String = ""
    @State private var lastSentValue: String = ""
    @State private var initialized: Bool = false
    @State private var debounceTask: Task<Void, Never>? = nil
    @FocusState private var isFocused: Bool

    var body: some View {
        let p = node.props
        let placeholder   = p.getString("placeholder")
        let serverValue   = p.getString("value")
        let secure        = p.getBool("secure")
        let multiline     = p.getBool("multiline")
        let maxLength     = p.getInt("max_length")
        let maxLines      = p.getInt("max_lines")
        let disabled      = p.getBool("disabled")
        let readOnly      = p.getBool("read_only")
        let keyboard      = resolveKeyboardType(p.getString("keyboard"))
        let onChangeCb    = p.getCallbackId("on_change")
        let onSubmitCb    = p.getCallbackId("on_submit")
        let syncMode      = p.getString("sync_mode", default: "live")
        let debounceMs    = p.getInt("debounce_ms", default: 300)

        // Apply `.foregroundColor` (not just `.foregroundStyle`) so the TYPED
        // text adopts `contentColor`. SwiftUI's TextField/SecureField don't
        // reliably pick up `.foregroundStyle` for the input text on older
        // iOS runtimes — `.foregroundColor` on the field itself always works.
        Group {
            if secure {
                SecureField(placeholder, text: $text)
                    .foregroundColor(contentColor)
                    .focused($isFocused)
            } else if multiline {
                TextField(placeholder, text: $text, axis: .vertical)
                    .lineLimit(maxLines > 0 ? 1...maxLines : 1...5)
                    .foregroundColor(contentColor)
                    .focused($isFocused)
            } else {
                TextField(placeholder, text: $text)
                    .foregroundColor(contentColor)
                    .focused($isFocused)
            }
        }
        .font(.system(size: textSize))
        .tint(tintColor)
        .keyboardType(keyboard)
        .disabled(disabled || readOnly)
        .submitLabel(onSubmitCb != 0 ? .done : .return)
        .onAppear {
            if !initialized {
                text = serverValue
                lastSentValue = serverValue
                initialized = true
            }
        }
        .onChange(of: serverValue) { _, newServerValue in
            // Only sync from server when the incoming value differs from what
            // we last sent. Matching == it's an echo of our own change; ignore
            // to avoid cursor jumps / clobbering in-flight edits.
            if newServerValue != lastSentValue {
                text = newServerValue
                lastSentValue = newServerValue
            }
        }
        .onChange(of: text) { _, newValue in
            let filtered = maxLength > 0 ? String(newValue.prefix(maxLength)) : newValue
            if filtered != newValue { text = filtered }
            handleLocalChange(filtered, mode: syncMode, debounceMs: debounceMs, onChangeCb: onChangeCb)
        }
        .onChange(of: isFocused) { _, focused in
            // On blur, flush any pending change — covers both `blur` mode
            // (never dispatched mid-typing) and `debounce` mode (in-flight
            // timer that should commit immediately rather than race with
            // focus loss / keyboard dismiss).
            if !focused {
                flushPending(onChangeCb: onChangeCb)
            }
        }
        .onSubmit {
            // Submit also acts as a commit point — flush pending, then
            // dispatch submit.
            flushPending(onChangeCb: onChangeCb)
            if onSubmitCb != 0 {
                NativeElementBridge.sendSubmitEvent(onSubmitCb, nodeId: node.id, text: text)
            }
        }
    }

    // ─── Dispatch policy ─────────────────────────────────────────────────────

    private func handleLocalChange(_ value: String, mode: String, debounceMs: Int, onChangeCb: Int) {
        switch mode {
        case "blur":
            // Don't dispatch mid-typing. `lastSentValue` stays anchored to
            // the last committed value so the echo-prevention check still
            // protects against programmatic server pushes that match the
            // committed state.
            return

        case "debounce":
            // Cancel any in-flight timer and schedule a fresh one. First
            // keystroke wins a fresh N ms budget; each subsequent keystroke
            // resets it. Final value is committed when the timer fires OR
            // when the field blurs (whichever comes first).
            debounceTask?.cancel()
            let captured = value
            let delayNanos = UInt64(max(50, debounceMs)) * 1_000_000
            debounceTask = Task { @MainActor in
                try? await Task.sleep(nanoseconds: delayNanos)
                if Task.isCancelled { return }
                commit(captured, onChangeCb: onChangeCb)
            }

        default: // "live"
            commit(value, onChangeCb: onChangeCb)
        }
    }

    private func flushPending(onChangeCb: Int) {
        debounceTask?.cancel()
        debounceTask = nil
        if text != lastSentValue {
            commit(text, onChangeCb: onChangeCb)
        }
    }

    private func commit(_ value: String, onChangeCb: Int) {
        lastSentValue = value
        if onChangeCb != 0 {
            NativeElementBridge.sendTextChangeEvent(onChangeCb, nodeId: node.id, text: value)
        }
    }
}

/// Keyboard resolution — accepts string hints ("email", "number", etc.) that
/// map to UIKeyboardType. Unknown/empty falls through to default.
private func resolveKeyboardType(_ kind: String) -> UIKeyboardType {
    switch kind.lowercased() {
    case "number":         return .numberPad
    case "email":          return .emailAddress
    case "phone":          return .phonePad
    case "url":            return .URL
    case "decimal":        return .decimalPad
    case "numberpassword": return .numberPad
    default:               return .default
    }
}
