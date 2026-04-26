import SwiftUI

struct NativeUIIconRenderer: View {
    let node: NativeUINode

    var body: some View {
        let p = node.props
        let name = p.getString("name")
        let size = CGFloat(p.getFloat("size", default: 24))
        let color = p.getColor("color", default: 0xFF000000)

        Image(systemName: getIconForName(name))
            .resizable()
            .aspectRatio(contentMode: .fit)
            .frame(width: size, height: size)
            .foregroundColor(Color(argb: color))
            .applyClickHandlers(node: node)
    }
}
