import SwiftUI

struct ComposeUIStackRenderer: View {
    let node: NativeUINode

    var body: some View {
        ZStack {
            ForEach(node.children) { child in
                NodeView(node: child)
                    .equatable()
            }
        }
    }
}
