import SwiftUI

struct NativeUIScrollViewRenderer: View {
    let node: NativeUINode

    var body: some View {
        let horizontal = node.props.getBool("horizontal")
        let showsIndicators = node.props.getBool("shows_indicators", default: true)
        let spacing = CGFloat(node.layout?.gap ?? 0)

        if horizontal {
            ScrollView(.horizontal, showsIndicators: showsIndicators) {
                LazyHStack(alignment: .top, spacing: spacing) {
                    ForEach(node.children) { child in
                        NodeView(node: child)
                            .equatable()
                    }
                }
            }
            .scrollDismissesKeyboard(.interactively)
        } else {
            ScrollView(.vertical, showsIndicators: showsIndicators) {
                LazyVStack(alignment: .leading, spacing: spacing) {
                    ForEach(node.children) { child in
                        NodeView(node: child)
                            .equatable()
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                }
                .frame(maxWidth: .infinity)
            }
            .scrollDismissesKeyboard(.interactively)
        }
    }
}
