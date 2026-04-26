import SwiftUI

/// SwiftUI Button renderer.
///
/// Maps semantic `variant` prop to the matching SwiftUI button style:
///   - primary     → `.buttonStyle(.borderedProminent)` + `.tint(theme.primary)`
///   - secondary   → `.buttonStyle(.bordered)` + `.tint(theme.secondary)`
///   - destructive → `Button(role: .destructive)` + `.buttonStyle(.borderedProminent)` + `.tint(theme.error)`
///   - ghost       → `.buttonStyle(.plain)` + `.foregroundStyle(theme.primary)`
///
/// All colors come from the `\.nativeUITheme` environment. No per-instance
/// color/radius/shadow overrides are honored — that's intentional (plan doc
/// Model 3). For full visual control, use `<native:pressable>`.
struct NativeUIButtonRenderer: View {
    let node: NativeUINode
    // Observe the shared store directly so PHP-pushed `NativeUI.Theme.Set`
    // updates trigger re-render. Reading via `@Environment(\.nativeUITheme)`
    // would be cleaner BUT nothing in the render tree provides it — so the
    // env default (.fallback) would always win.
    @ObservedObject private var themeStore = NativeUITheme.shared
    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        let theme = themeStore.resolve(for: colorScheme)
        let p = node.props
        let variant = p.getString("variant", default: "primary")
        let size = p.getString("size", default: "md")
        let label = p.getString("label")
        let disabled = p.getBool("disabled")
        let loading = p.getBool("loading")
        let icon = p.getString("leading_icon")
        let iconTrailing = p.getString("trailing_icon")
        let a11yLabel = p.getString("a11y_label")
        let a11yHint = p.getString("a11y_hint")
        let pressCb = p.getCallbackId("on_press") != 0 ? p.getCallbackId("on_press") : node.onPress

        let metrics = sizeMetrics(for: size, theme: theme)
        let enabled = !disabled && !loading

        let action = {
            if pressCb != 0 {
                NativeElementBridge.sendPressEvent(pressCb, nodeId: node.id)
            }
        }

        // Common content (icon + label + trailing icon, or a spinner + label when loading).
        let content = ButtonContent(
            label: label,
            icon: icon,
            iconTrailing: iconTrailing,
            loading: loading,
            iconSize: metrics.iconSize,
            textSize: metrics.textSize
        )

        // Variant-dispatched button. `.foregroundStyle(...)` is applied on the
        // outer Button view, AFTER `.buttonStyle(...)`, so it overrides the
        // auto-contrast content color SwiftUI picks internally (especially
        // visible on `.bordered`, where the system otherwise flips text to
        // black/white based on perceived tint luminance).
        switch variant {
        case "secondary":
            Button(action: action) { content }
                .buttonStyle(.bordered)
                .tint(theme.secondary)
                .foregroundStyle(theme.onSecondary)
                .controlSize(metrics.controlSize)
                .disabled(!enabled)
                .modifier(A11yLabelModifier(label: a11yLabel))
                .modifier(A11yHintModifier(hint: a11yHint))

        case "destructive":
            // Note: not using `role: .destructive` — it fights `.tint()` on
            // `.borderedProminent` and can render as the system destructive
            // color rather than the theme's destructive token.
            Button(action: action) { content }
                .buttonStyle(.borderedProminent)
                .tint(theme.destructive)
                .foregroundStyle(theme.onDestructive)
                .controlSize(metrics.controlSize)
                .disabled(!enabled)
                .modifier(A11yLabelModifier(label: a11yLabel))
                .modifier(A11yHintModifier(hint: a11yHint))

        case "ghost":
            Button(action: action) { content }
                .buttonStyle(.plain)
                .foregroundStyle(theme.primary)
                .controlSize(metrics.controlSize)
                .disabled(!enabled)
                .modifier(A11yLabelModifier(label: a11yLabel))
                .modifier(A11yHintModifier(hint: a11yHint))

        default: // "primary" and any unknown value
            Button(action: action) { content }
                .buttonStyle(.borderedProminent)
                .tint(theme.primary)
                .foregroundStyle(theme.onPrimary)
                .controlSize(metrics.controlSize)
                .disabled(!enabled)
                .modifier(A11yLabelModifier(label: a11yLabel))
                .modifier(A11yHintModifier(hint: a11yHint))
        }
    }

    // ─── Size metrics ────────────────────────────────────────────────────────

    private struct SizeMetrics {
        let controlSize: ControlSize
        let iconSize: CGFloat
        let textSize: CGFloat
    }

    private func sizeMetrics(for size: String, theme: NativeUITokens) -> SizeMetrics {
        switch size {
        case "sm":
            return SizeMetrics(controlSize: .small,   iconSize: 14, textSize: theme.fontSm)
        case "lg":
            return SizeMetrics(controlSize: .large,   iconSize: 22, textSize: theme.fontLg)
        default:
            return SizeMetrics(controlSize: .regular, iconSize: 18, textSize: theme.fontMd)
        }
    }
}

// MARK: - Content (label + icons, or spinner)

private struct ButtonContent: View {
    let label: String
    let icon: String
    let iconTrailing: String
    let loading: Bool
    let iconSize: CGFloat
    let textSize: CGFloat

    var body: some View {
        HStack(spacing: 8) {
            if loading {
                ProgressView()
                    .controlSize(.small)
                if !label.isEmpty {
                    Text(label).font(.system(size: textSize, weight: .medium))
                }
            } else {
                if !icon.isEmpty {
                    Image(systemName: getIconForName(icon))
                        .font(.system(size: iconSize))
                }
                if !label.isEmpty {
                    Text(label).font(.system(size: textSize, weight: .medium))
                }
                if !iconTrailing.isEmpty {
                    Image(systemName: getIconForName(iconTrailing))
                        .font(.system(size: iconSize))
                }
            }
        }
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