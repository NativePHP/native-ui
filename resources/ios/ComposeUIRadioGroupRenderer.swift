import SwiftUI

struct ComposeUIRadioGroupRenderer: View {
    let node: NativeUINode
    @State private var selectedValue: String = ""

    var body: some View {
        let onChangeCb = node.props.getCallbackId("on_change")

        VStack(alignment: .leading, spacing: 4) {
            ForEach(node.children.filter { $0.type == "radio" }) { child in
                ComposeUIRadioRenderer(
                    node: child,
                    selectedValue: selectedValue,
                    onSelect: { value in
                        selectedValue = value
                        if onChangeCb != 0 {
                            NativeUIBridge.sendRadioChangeEvent(onChangeCb, nodeId: node.id, value: value)
                        }
                    }
                )
            }
        }
        .onAppear {
            selectedValue = node.props.getString("value")
        }
    }
}

struct ComposeUIRadioRenderer: View {
    let node: NativeUINode
    let selectedValue: String
    let onSelect: (String) -> Void

    var body: some View {
        let p = node.props
        let value = p.getString("value")
        let label = p.getString("label")
        let labelColor = p.getColor("label_color", default: 0xFF000000)
        let disabled = p.getBool("disabled")
        let isSelected = selectedValue == value

        Button(action: {
            guard !disabled else { return }
            onSelect(value)
        }) {
            HStack(spacing: 8) {
                Image(systemName: isSelected ? "circle.inset.filled" : "circle")
                    .foregroundColor(isSelected ? .accentColor : .secondary)
                if !label.isEmpty {
                    Text(label)
                        .foregroundColor(Color(argb: labelColor))
                }
            }
        }
        .buttonStyle(.plain)
        .opacity(disabled ? 0.5 : 1.0)
    }
}
