import SwiftUI

struct ComposeUITabRowRenderer: View {
    let node: NativeUINode
    @State private var selectedIndex: Int = 0

    var body: some View {
        let onChangeCb = node.props.getCallbackId("on_change")
        let tabs = node.children.filter { $0.type == "tab" }

        if !tabs.isEmpty {
            VStack(spacing: 0) {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 0) {
                        ForEach(Array(tabs.enumerated()), id: \.element.id) { index, tab in
                            let label = tab.props.getString("label")
                            let icon = tab.props.getString("icon")
                            let isSelected = index == selectedIndex

                            Button(action: {
                                selectedIndex = index
                                if onChangeCb != 0 {
                                    NativeUIBridge.sendTabChangeEvent(onChangeCb, nodeId: node.id, index: index)
                                }
                            }) {
                                VStack(spacing: 4) {
                                    if !icon.isEmpty {
                                        Image(systemName: getIconForName(icon))
                                    }
                                    if !label.isEmpty {
                                        Text(label)
                                            .font(.subheadline)
                                    }
                                }
                                .padding(.horizontal, 16)
                                .padding(.vertical, 10)
                                .foregroundColor(isSelected ? .accentColor : .secondary)
                            }
                            .overlay(alignment: .bottom) {
                                if isSelected {
                                    Rectangle()
                                        .fill(Color.accentColor)
                                        .frame(height: 2)
                                }
                            }
                        }
                    }
                }
                Divider()
            }
        }
    }
}

/// No-op placeholder — tabs are rendered by TabRowRenderer
struct ComposeUITabRenderer: View {
    let node: NativeUINode
    var body: some View { EmptyView() }
}
