import SwiftUI

struct NativeUIScrollViewRenderer: View {
    let node: NativeUINode

    var body: some View {
        let horizontal = node.props.getBool("horizontal")
        let showsIndicators = node.props.getBool("shows_indicators", default: true)
        let spacing = CGFloat(node.layout?.gap ?? 0)
        let axis = node.props.getString("axis", default: "")

        // 2D mode. Bypass the Lazy stacks (which force 1D layout) and use a
        // plain ZStack so each child renders at its declared frame. The
        // child should have explicit `w-[N]` / `h-[N]` larger than the
        // viewport; SwiftUI's `ScrollView([.horizontal, .vertical])`
        // handles the panning.
        if axis == "both" {
            // 2D pan content. Wrapping in a SwiftUI stack (ZStack/VStack)
            // here causes the inner content to inherit the stack's
            // proposal-driven sizing — ScrollView then proposes its
            // viewport, the stack collapses, and the vertical axis
            // rubber-bands. Idiomatic SwiftUI 2D scrolling places the
            // content view directly inside the ScrollView so the content's
            // own `.frame(...)` (set by NodeLayoutModifier from `w-[N]` /
            // `h-[N]` classes) drives the scrollable size.
            //
            // Multi-child 2D scrolls are rare (typical use is one large
            // image / canvas). For multiple children we layer them in a
            // ZStack pinned via `.fixedSize` and accept that NavigationStack
            // may wobble on the vertical axis — author can wrap in a
            // single `<native:stack>` child as a workaround.
            ScrollView([.horizontal, .vertical], showsIndicators: showsIndicators) {
                if node.children.count == 1, let only = node.children.first {
                    NodeView(node: only).equatable()
                } else {
                    ZStack(alignment: .topLeading) {
                        ForEach(node.children) { child in
                            NodeView(node: child).equatable()
                        }
                    }
                    .fixedSize(horizontal: true, vertical: true)
                }
            }
            .scrollDismissesKeyboard(.interactively)
        } else if horizontal {
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
