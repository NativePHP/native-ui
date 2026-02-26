import SwiftUI

struct TextRenderer: View {
    let node: NativeUINode

    var body: some View {
        let text = node.props.getString("text")
        let fontSize = node.props.getFloat("font_size", default: 16)
        let fontWeight = resolveFontWeight(node.props.getInt("font_weight"))
        let color = node.props.getColor("color", default: 0xFF000000)
        let textAlign = resolveTextAlign(node.props.getInt("text_align"))
        let maxLines = node.props.getInt("max_lines")

        SwiftUI.Text(text)
            .font(.system(size: CGFloat(fontSize), weight: fontWeight))
            .foregroundColor(Color(argb: color))
            .multilineTextAlignment(textAlign)
            .lineLimit(maxLines > 0 ? maxLines : nil)
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
