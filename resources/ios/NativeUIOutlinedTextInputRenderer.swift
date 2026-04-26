import SwiftUI

/// Material3-style outlined text field (iOS / SwiftUI).
///
/// Composition:
///
///   ┌──────────────────────────────┐
///   │ ⎯Label⎯                      │   ← optional floating label
///   │ 🔍 placeholder/value     ✕   │   ← leading icon + core + trailing
///   └──────────────────────────────┘
///     supporting text               ← optional (error-colored if error)
///
/// All chrome colors resolve from `NativeUITheme.shared`. Per-instance color
/// overrides are intentionally not supported (Model 3 — drop to
/// `<native:pressable>` for fully custom input visuals).
struct NativeUIOutlinedTextInputRenderer: View {
    let node: NativeUINode

    @ObservedObject private var themeStore = NativeUITheme.shared
    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        let theme = themeStore.resolve(for: colorScheme)
        let p = node.props

        let label         = p.getString("label")
        let supporting    = p.getString("supporting")
        let prefixText    = p.getString("prefix")
        let suffixText    = p.getString("suffix")
        let leadingIcon   = p.getString("leading_icon")
        let trailingIcon  = p.getString("trailing_icon")
        let isError       = p.getBool("is_error")
        let disabled      = p.getBool("disabled")
        let readOnly      = p.getBool("read_only")
        let loading       = p.getBool("loading")
        let size          = p.getString("size", default: "md")
        let a11yLabel     = p.getString("a11y_label")
        let a11yHint      = p.getString("a11y_hint")

        let metrics = sizeMetrics(for: size, theme: theme)

        // Border / label color reflect state:
        //   error > focus-hint (we don't track focus here, so fall back to outline) > outline
        let borderColor: Color = isError
            ? theme.destructive
            : (disabled ? theme.outline.opacity(0.5) : theme.outline)

        let labelColor: Color = isError
            ? theme.destructive
            : theme.onSurfaceVariant

        let supportingColor: Color = isError ? theme.destructive : theme.onSurfaceVariant

        VStack(alignment: .leading, spacing: 4) {
            if !label.isEmpty {
                Text(label)
                    .font(.system(size: theme.fontSm, weight: .medium))
                    .foregroundStyle(labelColor)
            }

            HStack(spacing: 8) {
                if !leadingIcon.isEmpty {
                    Image(systemName: getIconForName(leadingIcon))
                        .font(.system(size: metrics.iconSize))
                        .foregroundStyle(theme.onSurfaceVariant)
                }
                if !prefixText.isEmpty {
                    Text(prefixText)
                        .font(.system(size: metrics.textSize))
                        .foregroundStyle(theme.onSurfaceVariant)
                }

                NativeUITextInputCore(
                    node: node,
                    textSize: metrics.textSize,
                    contentColor: disabled ? theme.onSurface.opacity(0.6) : theme.onSurface,
                    tintColor: isError ? theme.destructive : theme.primary
                )
                .frame(maxWidth: .infinity, alignment: .leading)

                if !suffixText.isEmpty {
                    Text(suffixText)
                        .font(.system(size: metrics.textSize))
                        .foregroundStyle(theme.onSurfaceVariant)
                }
                if loading {
                    ProgressView().controlSize(.small)
                } else if !trailingIcon.isEmpty {
                    Image(systemName: getIconForName(trailingIcon))
                        .font(.system(size: metrics.iconSize))
                        .foregroundStyle(theme.onSurfaceVariant)
                }
            }
            .padding(.horizontal, metrics.hPadding)
            .padding(.vertical, metrics.vPadding)
            .background(
                RoundedRectangle(cornerRadius: theme.radiusMd, style: .continuous)
                    .stroke(borderColor, lineWidth: isError ? 2 : 1)
            )
            .opacity(disabled ? 0.6 : 1.0)
            .allowsHitTesting(!disabled && !readOnly)

            if !supporting.isEmpty {
                Text(supporting)
                    .font(.system(size: theme.fontSm))
                    .foregroundStyle(supportingColor)
            }
        }
        .modifier(A11yLabelModifier(label: a11yLabel))
        .modifier(A11yHintModifier(hint: a11yHint))
    }

    // ─── Size metrics ────────────────────────────────────────────────────────

    private struct SizeMetrics {
        let textSize: CGFloat
        let iconSize: CGFloat
        let hPadding: CGFloat
        let vPadding: CGFloat
    }

    private func sizeMetrics(for size: String, theme: NativeUITokens) -> SizeMetrics {
        switch size {
        case "sm":
            return SizeMetrics(textSize: theme.fontSm, iconSize: 16, hPadding: 10, vPadding: 8)
        case "lg":
            return SizeMetrics(textSize: theme.fontLg, iconSize: 22, hPadding: 14, vPadding: 14)
        default:
            return SizeMetrics(textSize: theme.fontMd, iconSize: 18, hPadding: 12, vPadding: 11)
        }
    }
}

// MARK: - Accessibility modifiers (conditional)
// Note: duplicated from the button renderer to keep per-file drop-in usable.
// If this pattern spreads further we can lift these into a shared file.

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
