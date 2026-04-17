import SwiftUI

struct ComposeUIButtonGroupRenderer: View {
    let node: NativeUINode
    @State private var selectedIndex: Int = 0

    var body: some View {
        let p = node.props
        let options = p.getStringList("options")
        let onChangeCb = p.getCallbackId("on_change")
        let disabled = p.getBool("disabled")

        if !options.isEmpty {
            HStack(spacing: 0) {
                ForEach(Array(options.enumerated()), id: \.offset) { index, label in
                    let isSelected = index == selectedIndex
                    let isFirst = index == 0
                    let isLast = index == options.count - 1

                    Button(action: {
                        selectedIndex = index
                        if onChangeCb != 0 {
                            NativeUIBridge.sendTabChangeEvent(onChangeCb, nodeId: node.id, index: index)
                        }
                    }) {
                        Text(label)
                            .font(.subheadline.weight(.medium))
                            .padding(.horizontal, 16)
                            .padding(.vertical, 10)
                            .frame(maxWidth: .infinity)
                            .foregroundColor(isSelected ? .accentColor : .primary)
                            .background(isSelected ? Color.accentColor.opacity(0.12) : Color.clear)
                    }
                    .disabled(disabled)
                    .clipShape(
                        UnevenRoundedRectangle(
                            topLeadingRadius: isFirst ? 8 : 0,
                            bottomLeadingRadius: isFirst ? 8 : 0,
                            bottomTrailingRadius: isLast ? 8 : 0,
                            topTrailingRadius: isLast ? 8 : 0
                        )
                    )

                    if !isLast {
                        Divider().frame(height: 36)
                    }
                }
            }
            .overlay(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(Color(.separator), lineWidth: 1)
            )
            .onAppear {
                selectedIndex = node.props.getInt("selected_index")
            }
        }
    }
}
