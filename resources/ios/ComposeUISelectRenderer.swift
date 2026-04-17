import SwiftUI

struct ComposeUISelectRenderer: View {
    let node: NativeUINode
    @State private var selected: String = ""

    var body: some View {
        let p = node.props
        let options = p.getStringList("options")
        let onChangeCb = p.getCallbackId("on_change")
        let disabled = p.getBool("disabled")
        let placeholder = p.getString("placeholder")

        Menu {
            ForEach(options, id: \.self) { option in
                Button(option) {
                    selected = option
                    if onChangeCb != 0 {
                        NativeUIBridge.sendSelectChangeEvent(onChangeCb, nodeId: node.id, value: option)
                    }
                }
            }
        } label: {
            HStack {
                Text(selected.isEmpty ? placeholder : selected)
                    .foregroundColor(selected.isEmpty ? .secondary : .primary)
                Spacer()
                Image(systemName: "chevron.up.chevron.down")
                    .foregroundColor(.secondary)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background(Color(.systemGray6))
            .cornerRadius(8)
        }
        .disabled(disabled)
        .onAppear {
            selected = node.props.getString("value")
        }
    }
}
