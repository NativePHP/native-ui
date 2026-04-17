import SwiftUI

struct ComposeUICardRenderer: View {
    let node: NativeUINode

    var body: some View {
        let variant = node.props.getInt("variant")

        VStack(spacing: 0) {
            ForEach(node.children) { child in
                RenderNode(node: child)
            }
        }
        .background(Color(.systemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            Group {
                if variant == 1 {
                    // Outlined variant
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(Color(.separator), lineWidth: 1)
                }
            }
        )
        .shadow(radius: variant == 2 ? 4 : 2)
        .applyClickHandlers(node: node)
    }
}
