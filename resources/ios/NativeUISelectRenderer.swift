import SwiftUI

/// SwiftUI Select — popover picker using `Menu`.
///
/// Echo-prevention value sync (plan K) on the string option. Theme-sourced
/// colors (Model 3). Optional `label` text above the trigger.
struct NativeUISelectRenderer: View {
    let node: NativeUINode

    @ObservedObject private var themeStore = NativeUITheme.shared
    @Environment(\.colorScheme) private var colorScheme

    @State private var selected: String = ""
    @State private var lastSentValue: String = ""
    @State private var initialized: Bool = false

    var body: some View {
        let theme = themeStore.resolve(for: colorScheme)
        let p = node.props
        let serverValue = p.getString("value")
        let label       = p.getString("label")
        let options     = p.getStringList("options")
        let placeholder = p.getString("placeholder")
        let onChangeCb  = p.getCallbackId("on_change")
        let disabled    = p.getBool("disabled")
        let a11yLabel   = p.getString("a11y_label")
        let a11yHint    = p.getString("a11y_hint")

        VStack(alignment: .leading, spacing: 4) {
            if !label.isEmpty {
                Text(label)
                    .font(.system(size: theme.fontSm, weight: .medium))
                    .foregroundStyle(theme.onSurfaceVariant)
            }

            Menu {
                ForEach(options, id: \.self) { option in
                    Button(option) {
                        selected = option
                        lastSentValue = option
                        if onChangeCb != 0 {
                            NativeElementBridge.sendSelectChangeEvent(onChangeCb, nodeId: node.id, value: option)
                        }
                    }
                }
            } label: {
                HStack {
                    Text(selected.isEmpty ? placeholder : selected)
                        .foregroundStyle(selected.isEmpty ? theme.onSurfaceVariant : theme.onSurface)
                    Spacer()
                    Image(systemName: "chevron.up.chevron.down")
                        .foregroundStyle(theme.onSurfaceVariant)
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 11)
                .background(
                    RoundedRectangle(cornerRadius: theme.radiusMd, style: .continuous)
                        .stroke(theme.outline, lineWidth: 1)
                )
            }
            .disabled(disabled)
            .opacity(disabled ? 0.6 : 1.0)
        }
        .onAppear {
            if !initialized {
                selected = serverValue
                lastSentValue = serverValue
                initialized = true
            }
        }
        .onChange(of: serverValue) { _, new in
            if new != lastSentValue {
                selected = new
                lastSentValue = new
            }
        }
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
