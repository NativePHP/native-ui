import SwiftUI

struct NativeUITextRenderer: View {
    let node: NativeUINode

    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        let p = node.props
        let text = p.getString("text")
        let fontSize = p.getFloat("font_size", default: 16)
        let fontWeight = resolveFontWeight(p.getInt("font_weight"))
        let lightArgb = p.getColor("color", default: 0xFF000000)
        let darkArgb  = p.getColor("dark_color", default: 0)
        // Pick the dark hex when system is dark AND the theme class supplied
        // one (theme classes auto-emit a `dark` companion). Fall through to
        // the light value otherwise — matches NodeStyleModifier's bg/border
        // dark resolution semantics.
        let color: Int = (colorScheme == .dark && darkArgb != 0) ? darkArgb : lightArgb
        let textAlign = resolveTextAlign(p.getInt("text_align"))
        let maxLines = p.getInt("max_lines")

        if !text.isEmpty {
            Text(text)
                .font(.system(size: CGFloat(fontSize), weight: fontWeight))
                .foregroundColor(Color(argb: color))
                .multilineTextAlignment(textAlign)
                .lineLimit(maxLines > 0 ? maxLines : nil)
                // `truncationMode` only applies when there IS a lineLimit; we
                // therefore skip it in the unlimited case so SwiftUI doesn't
                // decide (on some iOS versions) that our `.frame(maxWidth:)`
                // means "single line, truncate" rather than "wrap within."
                .modifier(TruncateIfLimited(maxLines: maxLines))
                // Fill available horizontal space so:
                //  (1) Text can wrap at the container width instead of using
                //      its intrinsic one-line width;
                //  (2) `multilineTextAlignment` has space to align within.
                .frame(maxWidth: .infinity, alignment: frameAlignment(from: p.getInt("text_align")))
                // Grow vertically to fit wrapped content. Without this, SwiftUI
                // sometimes collapses the Text to a single line when bounded
                // by `.frame(maxWidth:)`.
                .fixedSize(horizontal: false, vertical: true)
        }
    }

    private func resolveFontWeight(_ weight: Int) -> Font.Weight {
        switch weight {
        case 1: return .thin
        case 2: return .light
        case 3: return .regular
        case 4: return .medium
        case 5: return .semibold
        case 6: return .bold
        case 7: return .heavy
        default: return .regular
        }
    }

    private func resolveTextAlign(_ align: Int) -> TextAlignment {
        switch align {
        case 0: return .leading
        case 1: return .center
        case 2: return .trailing
        default: return .leading
        }
    }

    /// Map text-align int to SwiftUI `Alignment` for use with `.frame(alignment:)`.
    private func frameAlignment(from align: Int) -> Alignment {
        switch align {
        case 1: return .center
        case 2: return .trailing
        default: return .leading
        }
    }
}

/// Applies `.truncationMode(.tail)` only when a line limit is actually set.
/// Applying it with `lineLimit(nil)` can make some SwiftUI versions behave
/// as if a single-line limit were in effect, collapsing multi-line content
/// into one truncated line.
private struct TruncateIfLimited: ViewModifier {
    let maxLines: Int
    func body(content: Content) -> some View {
        if maxLines > 0 {
            content.truncationMode(.tail)
        } else {
            content
        }
    }
}
