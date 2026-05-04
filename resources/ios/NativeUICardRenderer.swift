import SwiftUI

/// SwiftUI Card renderer.
///
/// Three variants mapped to SwiftUI surface idioms:
///   filled   — `theme.surfaceVariant` background, no stroke, minimal shadow
///   outlined — `theme.surface` background + `theme.outline` stroke
///   elevated — `theme.surface` background + soft shadow
///
/// The `glass` family of Tailwind classes opts the card's background out of
/// the variant's solid fill and uses Liquid Glass instead. iOS 26+ uses real
/// `.glassEffect(...)`; older iOS falls back to `.regularMaterial`. The card
/// is registered in `NodeStyleModifier.glassHandledByRenderer` so the outer
/// wrapper does NOT also paint a glass plate behind the rectangular node
/// frame.
///
/// Bitflags (matches `TailwindParser::parseGlassClass`):
///   bit 0 (1) — enabled
///   bit 1 (2) — prominent (no-op on cards — `.glassEffect()` has no
///                prominent variant; flag is reserved for buttons)
///   bit 2 (4) — interactive (touch-highlight feedback on the glass)
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

        let glassFlags = p.getInt("glass", default: 0)
        let glassEnabled     = (glassFlags & 1) != 0
        let glassInteractive = (glassFlags & 4) != 0
        let glassClear       = (glassFlags & 8) != 0

        // True when the user supplied a `bg-*` class. NodeStyleModifier
        // paints that color (with all its dark-mode + alpha machinery)
        // — we just need to know we shouldn't paint the variant default
        // OVER it. When false, we fall back to the variant's surface fill.
        let hasUserBg = (node.style?.bgColor ?? 0) != 0

        // Read border_radius from the node style — the PHP element defaults
        // to 16 (matching theme.radiusLg) so the outer NodeStyleModifier's
        // bg paint + ClipRadiusModifier clip to the same rounded shape this
        // renderer paints internally. User-supplied `rounded-*` classes
        // override the default and propagate to both the outer clip and
        // this inner surface.
        let styleRadius = CGFloat(node.style?.borderRadius ?? 0)
        let radius = styleRadius > 0 ? styleRadius : theme.radiusLg
        let shape = RoundedRectangle(cornerRadius: radius, style: .continuous)

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
        .modifier(CardBackgroundModifier(
            shape: shape,
            variant: variant,
            theme: theme,
            glassEnabled: glassEnabled,
            glassInteractive: glassInteractive,
            glassClear: glassClear,
            hasUserBg: hasUserBg
        ))
        .overlay(
            Group {
                if variant == "outlined" {
                    shape.stroke(theme.outline, lineWidth: 1)
                }
            }
        )
        // Glass surfaces don't take a drop-shadow well — the material does
        // its own depth. Only apply elevation shadow when not glass.
        .shadow(
            color: Color.black.opacity((variant == "elevated" && !glassEnabled) ? 0.12 : 0),
            radius: (variant == "elevated" && !glassEnabled) ? 6 : 0,
            x: 0, y: (variant == "elevated" && !glassEnabled) ? 2 : 0
        )
        .contentShape(shape)
        .applyClickHandlers(node: node)
        .modifier(A11yLabelModifier(label: a11yLabel))
        .modifier(A11yHintModifier(hint: a11yHint))
    }
}

/// Picks between the variant's solid fill and a Liquid Glass material based
/// on whether the `glass` bitflag is set. Single ViewModifier so the call site
/// stays a single chain.
private struct CardBackgroundModifier: ViewModifier {
    let shape: RoundedRectangle
    let variant: String
    let theme: NativeUITokens
    let glassEnabled: Bool
    let glassInteractive: Bool
    let glassClear: Bool
    /// True when the user supplied a `bg-*` class. We skip the variant
    /// fill in that case — NodeStyleModifier already paints the user's
    /// bg color and the variant default would just cover it up.
    let hasUserBg: Bool

    func body(content: Content) -> some View {
        if glassEnabled, #available(iOS 26.0, *) {
            // `.glassEffect()` paints behind the modified view (like a
            // background), with `in:` clipping to the supplied shape.
            // The outer NodeStyleModifier paints the user's bg color
            // beneath the glass — translucent glass on top filters it
            // through, producing the "tinted glass" effect.
            if glassClear {
                content.glassEffect(.clear.interactive(glassInteractive), in: shape)
            } else {
                content.glassEffect(.regular.interactive(glassInteractive), in: shape)
            }
        } else if glassEnabled {
            content.background(
                shape.fill(glassClear ? AnyShapeStyle(.ultraThinMaterial) : AnyShapeStyle(.regularMaterial))
            )
        } else if hasUserBg {
            // User bg wins — NodeStyleModifier already painted it. Don't
            // paint the variant default on top.
            content
        } else {
            content.background(
                shape.fill(variant == "filled" ? theme.surfaceVariant : theme.surface)
            )
        }
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
