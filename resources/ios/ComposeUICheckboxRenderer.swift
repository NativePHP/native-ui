import SwiftUI

struct ComposeUICheckboxRenderer: View {
    let node: NativeUINode
    @State private var checked: Bool = false

    var body: some View {
        let p = node.props
        let label = p.getString("label")
        let labelColor = p.getColor("label_color", default: 0xFF000000)
        let onChangeCb = p.getCallbackId("on_change")
        let disabled = p.getBool("disabled")

        Button(action: {
            guard !disabled else { return }
            checked.toggle()
            if onChangeCb != 0 {
                NativeUIBridge.sendCheckboxChangeEvent(onChangeCb, nodeId: node.id, value: checked)
            }
        }) {
            HStack(spacing: 8) {
                Image(systemName: checked ? "checkmark.square.fill" : "square")
                    .foregroundColor(checked ? .accentColor : .secondary)
                if !label.isEmpty {
                    Text(label)
                        .foregroundColor(Color(argb: labelColor))
                }
            }
        }
        .buttonStyle(.plain)
        .opacity(disabled ? 0.5 : 1.0)
        .onAppear {
            checked = node.props.getBool("value")
        }
    }
}
