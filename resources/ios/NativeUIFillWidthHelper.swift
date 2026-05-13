import SwiftUI

/// SwiftUI primitives like `Button`, `Toggle`, `Picker`, `TextField`,
/// `Slider`, `Stepper`, etc. ignore outer `.frame(maxWidth: .infinity)`
/// — they size to their label's intrinsic content regardless of any
/// frame wrapping them. `NodeLayoutModifier` correctly applies the
/// full-width frame at the NodeView level, but the inner primitive
/// paints small inside that frame.
///
/// The fix has to live INSIDE the renderer for each affected primitive:
/// apply `.frame(maxWidth: .infinity)` to the primitive's own view
/// when the node's layout says `widthMode == .fill`. This helper
/// centralizes the check so every renderer can opt in with one line.
///
/// Usage:
///
///     Button(action: action) { content }
///         .buttonStyle(.borderedProminent)
///         .fillWidthIfRequested(node)
///
/// Compose doesn't have this problem — Material 3 widgets honor
/// `Modifier.fillMaxWidth()` from their modifier chain directly, so
/// `NodeLayoutModifier`'s `Modifier.fillMaxWidth()` flows through.
extension View {
    func fillWidthIfRequested(_ node: NativeUINode) -> some View {
        modifier(FillWidthIfRequestedModifier(widthMode: node.layout?.widthMode))
    }
}

/// `SizeMode` in this codebase is a static-Int container (not a Swift
/// enum with cases), so `widthMode` is plain `Int?` and the constant
/// is `SizeMode.fill` (Int = 2). Avoid `.fill` shorthand because Swift
/// will infer it as SwiftUI's `ContentMode.fill` and the build breaks.
private struct FillWidthIfRequestedModifier: ViewModifier {
    let widthMode: Int?

    func body(content: Content) -> some View {
        if widthMode == SizeMode.fill {
            content.frame(maxWidth: .infinity)
        } else {
            content
        }
    }
}
