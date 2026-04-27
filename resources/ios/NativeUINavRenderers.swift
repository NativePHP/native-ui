import SwiftUI

// MARK: - Top Bar

struct NativeUITopBarRenderer: View {
    let node: NativeUINode

    var body: some View {
        let props = node.props
        let title = props.getString("title", default: "")
        let textColorArgb = props.getColor("text_color", default: 0)
        let textColor: Color = textColorArgb != 0 ? Color(argb: textColorArgb) : .primary
        let showBack = props.getBool("show_navigation_icon")

        HStack(spacing: 8) {
            // Leading: back button (system back, not a press callback —
            // the PHP runloop catches EventType.systemBack and pops the stack).
            if showBack {
                Button {
                    NativeElementBridge.sendSystemBackEvent()
                } label: {
                    Image(systemName: "chevron.backward")
                        .font(.system(size: 17, weight: .semibold))
                        .foregroundColor(textColor)
                        .frame(width: 32, height: 32, alignment: .leading)
                }
                .buttonStyle(.plain)
            }

            Text(title)
                .font(.headline)
                .foregroundColor(textColor)

            Spacer()

            ForEach(node.children.filter { $0.type == "top_bar_action" }) { action in
                let icon = action.props.getString("icon", default: "ellipsis")
                Button {
                    if action.onPress != 0 {
                        NativeElementBridge.sendPressEvent(action.onPress, nodeId: action.id)
                    }
                } label: {
                    Image(systemName: getIconForName(icon))
                        .foregroundColor(textColor)
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
    }
}

// MARK: - Bottom Nav

struct NativeUIBottomNavRenderer: View {
    let node: NativeUINode

    var body: some View {
        let items = node.children.filter { $0.type == "bottom_nav_item" }

        HStack {
            ForEach(items) { item in
                let label = item.props.getString("label", default: "")
                let icon = item.props.getString("icon", default: "circle")
                let active = item.props.getBool("active")

                Button {
                    if item.onPress != 0 {
                        NativeElementBridge.sendPressEvent(item.onPress, nodeId: item.id)
                    }
                } label: {
                    VStack(spacing: 4) {
                        Image(systemName: getIconForName(icon))
                            .font(.system(size: 24))
                        if !label.isEmpty {
                            Text(label)
                                .font(.caption2)
                        }
                    }
                    .foregroundColor(active ? .blue : .gray)
                    .frame(maxWidth: .infinity)
                }
            }
        }
        .padding(.vertical, 8)
    }
}

// MARK: - Side Nav (stores node for drawer)

struct NativeUISideNavRenderer: View {
    let node: NativeUINode

    var body: some View {
        // Side nav content is rendered by the drawer scaffold, not inline
        EmptyView()
    }
}
