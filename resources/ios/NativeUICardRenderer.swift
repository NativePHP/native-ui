import SwiftUI

/// SwiftUI Card renderer.
///
/// Three variants mapped to SwiftUI surface idioms:
///   filled   — `theme.surfaceVariant` background, no stroke, minimal shadow
///   outlined — `theme.surface` background + `theme.outline` stroke
///   elevated — `theme.surface` background + soft shadow
///
/// Model 3 — colors + radius come from theme tokens. `@press` / `@longPress`
/// are honored via `applyClickHandlers`.
struct NativeUICardRenderer: View {
    let node: NativeUINode

    @ObservedObject private var themeStore = NativeUITheme.shared
    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        let theme = themeStore.resolve(for: colorScheme)
        let p = node.props
        let variant = p.getString("variant", default: "filled")
        let a11yLabel = p.getString("a11y_label")
        let a11yHint  = p.getString("a11y_hint")

        let radius = theme.radiusLg

        // Content renders freely; the rounded-rect background/overlay draws
        // the visible card shape. Avoid `.clipShape(RoundedRectangle)` on the
        // outer stack — it trims glyphs whose ascender bounds extend into
        // the corner curve (seen as a chopped first letter on short headings
        // when the child's padding is near the radius).
        VStack(spacing: 0) {
            ForEach(node.children) { child in
                RenderNode(node: child)
            }
        }
        .background(
            RoundedRectangle(cornerRadius: radius, style: .continuous)
                .fill(variant == "filled" ? theme.surfaceVariant : theme.surface)
        )
        .overlay(
            Group {
                if variant == "outlined" {
                    RoundedRectangle(cornerRadius: radius, style: .continuous)
                        .stroke(theme.outline, lineWidth: 1)
                }
            }
        )
        .shadow(
            color: Color.black.opacity(variant == "elevated" ? 0.12 : 0),
            radius: variant == "elevated" ? 6 : 0,
            x: 0, y: variant == "elevated" ? 2 : 0
        )
        .contentShape(RoundedRectangle(cornerRadius: radius, style: .continuous))
        .applyClickHandlers(node: node)
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
