import SwiftUI

struct ComposeUITextRenderer: View {
    let node: NativeUINode

    var body: some View {
        let p = node.props
        let text = p.getString("text")
        let fontSize = p.getFloat("font_size", default: 16)
        let fontWeight = resolveFontWeight(p.getInt("font_weight"))
        let color = p.getColor("color", default: 0xFF000000)
        let textAlign = resolveTextAlign(p.getInt("text_align"))
        let maxLines = p.getInt("max_lines")

        if !text.isEmpty {
            Text(text)
                .font(.system(size: CGFloat(fontSize), weight: fontWeight))
                .foregroundColor(Color(argb: color))
                .multilineTextAlignment(textAlign)
                .lineLimit(maxLines > 0 ? maxLines : nil)
                .truncationMode(.tail)
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
}
