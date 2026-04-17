import SwiftUI

struct ComposeUISliderRenderer: View {
    let node: NativeUINode
    @State private var value: Double = 0

    var body: some View {
        let p = node.props
        let min = Double(p.getFloat("min", default: 0))
        let max = Double(p.getFloat("max", default: 1))
        let step = Double(p.getFloat("step"))
        let onChangeCb = p.getCallbackId("on_change")
        let disabled = p.getBool("disabled")
        let color = p.getColor("color", default: 0)

        Group {
            if step > 0 {
                Slider(value: $value, in: min...max, step: step) {
                    EmptyView()
                } onEditingChanged: { editing in
                    if !editing && onChangeCb != 0 {
                        NativeUIBridge.sendSliderChangeEvent(onChangeCb, nodeId: node.id, value: Float(value))
                    }
                }
            } else {
                Slider(value: $value, in: min...max) {
                    EmptyView()
                } onEditingChanged: { editing in
                    if !editing && onChangeCb != 0 {
                        NativeUIBridge.sendSliderChangeEvent(onChangeCb, nodeId: node.id, value: Float(value))
                    }
                }
            }
        }
        .disabled(disabled)
        .tint(color != 0 ? Color(argb: color) : nil)
        .onAppear {
            value = Double(node.props.getFloat("value"))
        }
    }
}
