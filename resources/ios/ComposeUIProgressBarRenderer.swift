import SwiftUI

struct ComposeUIProgressBarRenderer: View {
    let node: NativeUINode

    var body: some View {
        let p = node.props
        let value = Double(p.getFloat("value")).clamped(to: 0...1)
        let color = p.getColor("color", default: 0xFF007AFF)
        let trackColor = p.getColor("track_color", default: 0xFFE0E0E0)

        ProgressView(value: value)
            .tint(Color(argb: color))
            .background(Color(argb: trackColor))
    }
}
