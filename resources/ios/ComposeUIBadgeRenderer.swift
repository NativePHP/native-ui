import SwiftUI

struct ComposeUIBadgeRenderer: View {
    let node: NativeUINode

    var body: some View {
        let p = node.props
        let count = p.getInt("count")
        let color = p.getColor("color", default: 0xFFFF0000)
        let textColor = p.getColor("text_color", default: 0xFFFFFFFF)

        Text(count > 99 ? "99+" : "\(count)")
            .font(.system(size: 12, weight: .bold))
            .foregroundColor(Color(argb: textColor))
            .padding(.horizontal, 6)
            .padding(.vertical, 2)
            .background(Color(argb: color))
            .clipShape(Capsule())
    }
}
