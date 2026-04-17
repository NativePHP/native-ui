import SwiftUI

struct ComposeUIToggleRenderer: View {
    let node: NativeUINode
    @State private var isOn: Bool = false

    var body: some View {
        let p = node.props
        let onChangeCb = p.getCallbackId("on_change")
        let disabled = p.getBool("disabled")
        let label = p.getString("label")

        Toggle(label, isOn: $isOn)
            .disabled(disabled)
            .onChange(of: isOn) { _, newValue in
                if onChangeCb != 0 {
                    NativeUIBridge.sendToggleChangeEvent(onChangeCb, nodeId: node.id, value: newValue)
                }
            }
            .onAppear {
                isOn = node.props.getBool("value")
            }
    }
}
