import SwiftUI

struct ButtonRenderer: View {
    let node: NativeUINode

    var body: some View {
        let label = node.props.getString("label", default: "")
        let disabled = node.props.getBool("disabled", default: false)

        SwiftUI.Button(action: {
            if let pressCbId = node.props.getCallbackId("on_press"), pressCbId != 0 {
                NativeUIBridge.sendPressEvent(pressCbId, nodeId: node.id)
            } else if node.onPress != 0 {
                NativeUIBridge.sendPressEvent(node.onPress, nodeId: node.id)
            }
        }) {
            Text(label)
        }
        .disabled(disabled)
    }
}