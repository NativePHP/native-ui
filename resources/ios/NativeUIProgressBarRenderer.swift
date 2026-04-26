import SwiftUI

/// SwiftUI linear progress bar. Determinate when `value` is supplied;
/// indeterminate otherwise. Theme-tinted (Model 3).
struct NativeUIProgressBarRenderer: View {
    let node: NativeUINode

    @ObservedObject private var themeStore = NativeUITheme.shared
    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        let theme = themeStore.resolve(for: colorScheme)
        let p = node.props
        let indeterminate = p.getBool("indeterminate")
        let value = Double(p.getFloat("value")).clamped(to: 0...1)
        let a11yLabel = p.getString("a11y_label")

        let overrideArgb = p.getColor("color", default: 0)
        let tint: Color = overrideArgb != 0 ? Color(argb: overrideArgb) : theme.primary

        Group {
            if indeterminate {
                ProgressView()
            } else {
                ProgressView(value: value)
            }
        }
        .tint(tint)
        .modifier(A11yLabelModifier(label: a11yLabel))
    }
}

private struct A11yLabelModifier: ViewModifier {
    let label: String
    func body(content: Content) -> some View {
        if label.isEmpty { content }
        else { content.accessibilityLabel(label) }
    }
}
