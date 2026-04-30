import SwiftUI

struct NativeUIIconRenderer: View {
    let node: NativeUINode

    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        let p = node.props
        let name = p.getString("name")
        let size = CGFloat(p.getFloat("size", default: 24))
        let lightArgb = p.getColor("color", default: 0xFF000000)
        let darkArgb  = p.getColor("dark_color", default: 0)

        // When dark mode is active and a `dark-color` was supplied, use it;
        // otherwise fall through to the regular `color`. Same shape as
        // NodeStyleModifier's bg / border dark resolution so authoring stays
        // consistent across element types.
        let effectiveArgb: Int = (colorScheme == .dark && darkArgb != 0)
            ? darkArgb
            : lightArgb

        Image(systemName: getIconForName(name))
            .resizable()
            .aspectRatio(contentMode: .fit)
            .frame(width: size, height: size)
            .foregroundColor(Color(argb: effectiveArgb))
            .applyClickHandlers(node: node)
    }
}
