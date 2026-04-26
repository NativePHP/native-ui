import SwiftUI

/// SwiftUI circular activity indicator. Always indeterminate. Theme-tinted
/// (Model 3). Size variants scale the default ProgressView spinner.
struct NativeUIActivityIndicatorRenderer: View {
    let node: NativeUINode

    @ObservedObject private var themeStore = NativeUITheme.shared
    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        let theme = themeStore.resolve(for: colorScheme)
        let p = node.props
        let size = p.getString("size", default: "md")
        let a11yLabel = p.getString("a11y_label")

        // Optional override — primitives like spinners sometimes need to
        // match their container. When unset (color == 0), fall through to
        // the theme's primary tint.
        let overrideArgb = p.getColor("color", default: 0)
        let tint: Color = overrideArgb != 0 ? Color(argb: overrideArgb) : theme.primary

        let scale: CGFloat = switch size {
        case "lg": 1.5
        case "sm": 0.7
        default:   1.0
        }

        ProgressView()
            .scaleEffect(scale)
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
