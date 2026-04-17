import SwiftUI

struct ComposeUIActivityIndicatorRenderer: View {
    let node: NativeUINode

    var body: some View {
        let p = node.props
        let size = p.getInt("size")
        let color = p.getColor("color", default: 0xFF007AFF)

        let scale: CGFloat = switch size {
        case 1: 1.5   // large
        case 2: 0.7   // small
        default: 1.0  // medium
        }

        ProgressView()
            .scaleEffect(scale)
            .tint(Color(argb: color))
    }
}
