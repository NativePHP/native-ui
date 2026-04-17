import SwiftUI

struct ComposeUIButtonRenderer: View {
    let node: NativeUINode

    var body: some View {
        let p = node.props
        let label = p.getString("label")
        let disabled = p.getBool("disabled")
        let labelColor = p.getColor("label_color", default: 0xFFFFFFFF)
        let fontSize = p.getFloat("font_size")

        Text(label)
            .font(.system(size: fontSize > 0 ? CGFloat(fontSize) : 16, weight: .medium))
            .foregroundColor(Color(argb: labelColor))
            .multilineTextAlignment(.center)
            .opacity(disabled ? 0.5 : 1.0)
    }
}
