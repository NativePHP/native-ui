import SwiftUI

/// Themed screen backdrop.
///
/// Applies `theme.background` as the full-bleed background and sets the
/// default content color to `theme.onBackground` for all descendants. Use
/// as the root of a page:
///
///     <native:screen>
///         <native:scroll-view>...</native:scroll-view>
///     </native:screen>
///
/// Dark mode is handled automatically — we resolve from the shared theme
/// store against the current color scheme.
struct NativeUIScreenRenderer: View {
    let node: NativeUINode
    @ObservedObject private var themeStore = NativeUITheme.shared
    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        let theme = themeStore.resolve(for: colorScheme)

        // Children render in their natural layout. Background is painted
        // BEHIND content via the Color-view `.background(alignment:)` form so
        // it extends edge-to-edge (ignoresSafeArea) without forcing my own
        // frame constraints that could fight the scroll-view / column layout
        // of descendants.
        VStack(spacing: 0) {
            ForEach(node.children) { child in
                RenderNode(node: child)
            }
        }
        .frame(maxWidth: .infinity, alignment: .topLeading)
        .background(alignment: .topLeading) {
            theme.background.ignoresSafeArea()
        }
        .foregroundStyle(theme.onBackground)
    }
}
