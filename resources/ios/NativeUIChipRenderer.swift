import SwiftUI

/// SwiftUI Chip — compact selectable tag. Capsule with optional leading icon.
///
/// Echo-prevention (plan K) on bool selected state. Theme-sourced colors —
/// primary for active, surfaceVariant + outline for inactive (Model 3).
struct NativeUIChipRenderer: View {
    let node: NativeUINode

    @ObservedObject private var themeStore = NativeUITheme.shared
    @Environment(\.colorScheme) private var colorScheme

    @State private var isSelected: Bool = false
    @State private var lastSentValue: Bool = false
    @State private var initialized: Bool = false

    var body: some View {
        let theme = themeStore.resolve(for: colorScheme)
        let p = node.props
        let serverValue = p.getBool("value")
        let label       = p.getString("label")
        let iconName    = p.getString("icon")
        let onChangeCb  = p.getCallbackId("on_change")
        let disabled    = p.getBool("disabled")
        let a11yLabel   = p.getString("a11y_label")
        let a11yHint    = p.getString("a11y_hint")

        let bg = isSelected ? theme.primary : theme.surfaceVariant
        let fg = isSelected ? theme.onPrimary : theme.onSurface
        let border = isSelected ? theme.primary : theme.outline

        Button(action: {
            guard !disabled else { return }
            let new = !isSelected
            isSelected = new
            lastSentValue = new
            if onChangeCb != 0 {
                NativeElementBridge.sendToggleChangeEvent(onChangeCb, nodeId: node.id, value: new)
            }
        }) {
            HStack(spacing: 6) {
                if !iconName.isEmpty {
                    Image(systemName: getIconForName(iconName))
                        .font(.system(size: 14))
                }
                Text(label).font(.system(size: theme.fontSm, weight: .medium))
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .foregroundColor(fg)
            .background(Capsule().fill(bg))
            .overlay(Capsule().stroke(border, lineWidth: 1))
        }
        .buttonStyle(.plain)
        .disabled(disabled)
        .opacity(disabled ? 0.5 : 1.0)
        .onAppear {
            if !initialized {
                isSelected = serverValue
                lastSentValue = serverValue
                initialized = true
            }
        }
        .onChange(of: serverValue) { _, new in
            if new != lastSentValue {
                isSelected = new
                lastSentValue = new
            }
        }
        .accessibilityAddTraits(isSelected ? [.isButton, .isSelected] : .isButton)
        .modifier(A11yLabelModifier(label: a11yLabel))
        .modifier(A11yHintModifier(hint: a11yHint))
    }
}

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
