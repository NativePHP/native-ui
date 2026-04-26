import SwiftUI

/// SwiftUI Badge — capsule pill with count or short label.
///
/// Variant-dispatched colors from the theme (Model 3). No per-instance
/// color overrides.
struct NativeUIBadgeRenderer: View {
    let node: NativeUINode

    @ObservedObject private var themeStore = NativeUITheme.shared
    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        let theme = themeStore.resolve(for: colorScheme)
        let p = node.props
        let count     = p.getInt("count")
        let label     = p.getString("label")
        let variant   = p.getString("variant", default: "destructive")
        let a11yLabel = p.getString("a11y_label")

        let text = !label.isEmpty
            ? label
            : (count > 99 ? "99+" : "\(count)")

        let (bg, fg): (Color, Color) = {
            switch variant {
            case "primary":     return (theme.primary,     theme.onPrimary)
            case "accent":      return (theme.accent,      theme.onAccent)
            default:            return (theme.destructive, theme.onDestructive) // "destructive"
            }
        }()

        Text(text)
            .font(.system(size: 12, weight: .bold))
            .foregroundColor(fg)
            .padding(.horizontal, 6)
            .padding(.vertical, 2)
            .background(Capsule().fill(bg))
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
