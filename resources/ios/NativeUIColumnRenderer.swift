import SwiftUI

struct NativeUIColumnRenderer: View {
    let node: NativeUINode

    var body: some View {
        if node.children.isEmpty {
            Color.clear
        } else {
            let dir = node.layout?.flexDirection ?? FlexDirection.column
            FlexContainer(
                direction: dir,
                justify: node.layout?.justifyContent ?? JustifyContent.start,
                align: node.layout?.alignItems ?? AlignItems.stretch,
                gap: CGFloat(node.layout?.gap ?? 0),
                wrap: node.layout?.flexWrap ?? 0,
                childNodes: node.children
            ) {
                ForEach(node.children) { child in
                    NodeView(node: child)
                        .equatable()
                }
            }
        }
    }
}
