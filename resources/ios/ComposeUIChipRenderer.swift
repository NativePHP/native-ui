import SwiftUI

struct ComposeUIChipRenderer: View {
    let node: NativeUINode
    @State private var isSelected: Bool = false

    var body: some View {
        let p = node.props
        let label = p.getString("label")
        let onChangeCb = p.getCallbackId("on_change")
        let iconName = p.getString("icon")

        Button(action: {
            isSelected.toggle()
            if onChangeCb != 0 {
                NativeUIBridge.sendToggleChangeEvent(onChangeCb, nodeId: node.id, value: isSelected)
            }
        }) {
            HStack(spacing: 6) {
                if !iconName.isEmpty {
                    Image(systemName: getIconForName(iconName))
                        .font(.system(size: 14))
                }
                Text(label)
                    .font(.subheadline)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(isSelected ? Color.accentColor.opacity(0.15) : Color(.systemGray6))
            .foregroundColor(isSelected ? .accentColor : .primary)
            .clipShape(Capsule())
            .overlay(
                Capsule()
                    .stroke(isSelected ? Color.accentColor : Color(.separator), lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
        .onAppear {
            isSelected = node.props.getBool("selected")
        }
    }
}
