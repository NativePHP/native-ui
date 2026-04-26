import SwiftUI

/// SwiftUI Checkbox renderer.
///
/// SwiftUI has no native checkbox primitive — rendered as a tappable SF
/// Symbol pair (`checkmark.square.fill` / `square`) with an optional inline
/// label. Echo-prevention (plan K) for two-way binding; theme-sourced colors
/// (Model 3).
struct NativeUICheckboxRenderer: View {
    let node: NativeUINode

    @ObservedObject private var themeStore = NativeUITheme.shared
    @Environment(\.colorScheme) private var colorScheme

    @State private var checked: Bool = false
    @State private var lastSentValue: Bool = false
    @State private var initialized: Bool = false

    var body: some View {
        let theme = themeStore.resolve(for: colorScheme)
        let p = node.props
        let serverValue = p.getBool("value")
        let label       = p.getString("label")
        let onChangeCb  = p.getCallbackId("on_change")
        let disabled    = p.getBool("disabled")
        let a11yLabel   = p.getString("a11y_label")
        let a11yHint    = p.getString("a11y_hint")

        Button(action: {
            guard !disabled else { return }
            let new = !checked
            checked = new
            lastSentValue = new
            if onChangeCb != 0 {
                NativeElementBridge.sendCheckboxChangeEvent(onChangeCb, nodeId: node.id, value: new)
            }
        }) {
            HStack(spacing: 8) {
                Image(systemName: checked ? "checkmark.square.fill" : "square")
                    .font(.system(size: 22))
                    .foregroundColor(checked ? theme.primary : theme.onSurfaceVariant)
                if !label.isEmpty {
                    Text(label)
                        .foregroundColor(theme.onSurface)
                }
            }
        }
        .buttonStyle(.plain)
        .disabled(disabled)
        .opacity(disabled ? 0.5 : 1.0)
        .onAppear {
            if !initialized {
                checked = serverValue
                lastSentValue = serverValue
                initialized = true
            }
        }
        .onChange(of: serverValue) { _, new in
            if new != lastSentValue {
                checked = new
                lastSentValue = new
            }
        }
        .accessibilityAddTraits(.isButton)
        .modifier(A11yLabelModifier(label: a11yLabel))
        .modifier(A11yHintModifier(hint: a11yHint))
    }
}

// MARK: - Accessibility modifiers (conditional)

private struct A11yLabelModifier: ViewModifier {
    let label: String
    func body(content: Content) -> some View {
        if label.isEmpty { content }
        else { content.accessibilityLabel(label) }
    }
}

private struct A11yHintModifier: ViewModifier {
    let hint: String
    func body(content: Content) -> some View {
        if hint.isEmpty { content }
        else { content.accessibilityHint(hint) }
    }
}
