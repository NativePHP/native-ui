import SwiftUI

/// Custom Layout for `<native:stack>` — z-layers children centered within
/// the stack's bounds at their natural (or explicitly fill) sizes.
///
/// Why not a plain `ZStack`? `ZStack` proposes its own bounds to each
/// child. Any child whose `NodeLayoutModifier` carries `maxWidth: .infinity`
/// (the default for nodes that don't set an explicit width) will inflate
/// its frame to fill the stack. Combined with the modifier's
/// `alignment: .topLeading`, that pushes the actual content (e.g. an
/// `<native:icon>` glyph) to the leading edge — even though ZStack's own
/// alignment is `.center`. The visible symptom is "icon-on-the-left"
/// inside any stack that mixes a small intrinsic-sized child with a
/// larger sibling.
///
/// This Layout sidesteps that by:
///  1. Sizing each child via `.unspecified` (so its frame doesn't inflate).
///  2. Honoring `widthMode == fill` / `heightMode == fill` if the child
///     explicitly opted in (e.g. `class="w-full"`).
///  3. Centering each child within the stack's bounds.
struct NativeUIStackLayout: Layout {
    let childNodes: [NativeUINode]

    func sizeThatFits(
        proposal: ProposedViewSize,
        subviews: Subviews,
        cache: inout ()
    ) -> CGSize {
        var maxSize = CGSize.zero
        for subview in subviews {
            let size = subview.sizeThatFits(.unspecified)
            maxSize.width = max(maxSize.width, size.width)
            maxSize.height = max(maxSize.height, size.height)
        }
        return CGSize(
            width: proposal.width ?? maxSize.width,
            height: proposal.height ?? maxSize.height
        )
    }

    func placeSubviews(
        in bounds: CGRect,
        proposal: ProposedViewSize,
        subviews: Subviews,
        cache: inout ()
    ) {
        for (i, subview) in subviews.enumerated() {
            let layout = i < childNodes.count ? childNodes[i].layout : nil
            let widthFill = layout?.widthMode == SizeMode.fill
            let heightFill = layout?.heightMode == SizeMode.fill

            let natural = subview.sizeThatFits(.unspecified)
            let width = widthFill ? bounds.width : natural.width
            let height = heightFill ? bounds.height : natural.height

            let x = bounds.minX + (bounds.width - width) / 2
            let y = bounds.minY + (bounds.height - height) / 2

            subview.place(
                at: CGPoint(x: x, y: y),
                proposal: ProposedViewSize(width: width, height: height)
            )
        }
    }
}

struct NativeUIStackRenderer: View {
    let node: NativeUINode

    var body: some View {
        NativeUIStackLayout(childNodes: node.children) {
            ForEach(node.children) { child in
                NodeView(node: child)
                    .equatable()
            }
        }
    }
}
