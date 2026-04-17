import SwiftUI

struct ComposeUIPressableRenderer: View {
    let node: NativeUINode
    var body: some View {
        ComposeUIColumnRenderer(node: node)
    }
}

struct ComposeUICanvasRenderer: View {
    let node: NativeUINode
    var body: some View {
        ComposeUIColumnRenderer(node: node)
    }
}

struct ComposeUISpacerRenderer: View {
    let node: NativeUINode
    var body: some View {
        Spacer()
    }
}

struct ComposeUIDividerRenderer: View {
    let node: NativeUINode
    var body: some View {
        let borderArgb = node.style?.borderColor ?? 0
        let color: Color = borderArgb != 0 ? Color(argb: borderArgb) : Color(uiColor: .separator)
        Rectangle().fill(color).frame(height: 1)
    }
}

struct ComposeUIRectRenderer: View {
    let node: NativeUINode
    var body: some View {
        Rectangle().fill(.clear)
    }
}

struct ComposeUICircleRenderer: View {
    let node: NativeUINode
    var body: some View {
        Circle().fill(.clear)
    }
}

struct ComposeUILineRenderer: View {
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

struct ComposeUIImageRenderer: View {
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

struct ComposeUIEmptyRenderer: View {
    let node: NativeUINode
    var body: some View {
        EmptyView()
    }
}
