import SwiftUI

struct NativeUIPressableRenderer: View {
    let node: NativeUINode
    var body: some View {
        NativeUIColumnRenderer(node: node)
    }
}

struct NativeUICanvasRenderer: View {
    let node: NativeUINode
    var body: some View {
        NativeUIColumnRenderer(node: node)
    }
}

struct NativeUISpacerRenderer: View {
    let node: NativeUINode
    var body: some View {
        // SwiftUI's `Spacer()` only expands inside SwiftUI's own HStack/VStack —
        // our FlexContainer is a custom Layout, so a real Spacer would size to
        // zero. Color.clear accepts whatever proposal FlexContainer gives it
        // (driven by the spacer node's flex_grow=1 default), so it claims the
        // remaining main-axis space and pushes siblings apart as expected.
        Color.clear
    }
}

struct NativeUIDividerRenderer: View {
    let node: NativeUINode
    var body: some View {
        let borderArgb = node.style?.borderColor ?? 0
        let color: Color = borderArgb != 0 ? Color(argb: borderArgb) : Color(uiColor: .separator)
        Rectangle().fill(color).frame(height: 1)
    }
}

struct NativeUIRectRenderer: View {
    let node: NativeUINode
    var body: some View {
        // Shape primitive — renders as a filled rectangle using node.style.bgColor.
        // Border radius / stroke come from NodeStyleModifier above, so this only
        // paints the fill. `.fill(.clear)` (previous behavior) produced an
        // invisible shape.
        let fillArgb = node.style?.bgColor ?? 0
        let fill = fillArgb != 0 ? Color(argb: fillArgb) : Color.clear
        Rectangle().fill(fill)
    }
}

struct NativeUICircleRenderer: View {
    let node: NativeUINode
    var body: some View {
        let fillArgb = node.style?.bgColor ?? 0
        let fill = fillArgb != 0 ? Color(argb: fillArgb) : Color.clear
        Circle().fill(fill)
    }
}

struct NativeUILineRenderer: View {
    let node: NativeUINode
    var body: some View {
        let borderArgb = node.style?.borderColor ?? 0
        let color: Color = borderArgb != 0 ? Color(argb: borderArgb) : Color(uiColor: .separator)
        let width = CGFloat(node.style?.borderWidth ?? 1)
        Path { path in
            path.move(to: .zero)
            path.addLine(to: CGPoint(x: 100, y: 0))
        }
        .stroke(color, lineWidth: width)
    }
}

struct NativeUIImageRenderer: View {
    let node: NativeUINode
    var body: some View {
        let p = node.props
        let src = p.getString("src")
        let fit = p.getInt("fit")
        let tintArgb = p.getColor("tint_color", default: 0)
        let contentMode = resolveContentMode(fit)

        if let url = URL(string: src), !src.isEmpty {
            AsyncImage(url: url) { phase in
                switch phase {
                case .success(let image):
                    let img = image.resizable().aspectRatio(contentMode: contentMode)
                    if tintArgb != 0 {
                        img.foregroundStyle(Color(argb: tintArgb))
                    } else {
                        img
                    }
                case .failure:
                    Color.clear
                case .empty:
                    ProgressView()
                @unknown default:
                    Color.clear
                }
            }
        } else {
            Color.clear
        }
    }

    private func resolveContentMode(_ fit: Int) -> ContentMode {
        switch fit {
        case 2: return .fill
        case 3: return .fill
        default: return .fit
        }
    }
}

struct NativeUIEmptyRenderer: View {
    let node: NativeUINode
    var body: some View {
        EmptyView()
    }
}
