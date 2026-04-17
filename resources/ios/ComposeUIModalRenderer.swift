import SwiftUI

struct ComposeUIModalRenderer: View {
    let node: NativeUINode

    var body: some View {
        let visible = node.props.getBool("visible")
        let dismissible = node.props.getBool("dismissible", default: true)
        let onDismissCb = node.props.getCallbackId("on_dismiss")
        let nodeId = node.id

        Color.clear
            .frame(width: 0, height: 0)
            .fullScreenCover(isPresented: .constant(visible)) {
                VStack(spacing: 0) {
                    if dismissible {
                        HStack {
                            Spacer()
                            Button {
                                if onDismissCb != 0 {
                                    NativeElementBridge.sendPressEvent(onDismissCb, nodeId: nodeId)
                                }
                            } label: {
                                Image(systemName: "xmark.circle.fill")
                                    .font(.title2)
                                    .foregroundColor(.secondary)
                            }
                            .padding()
                        }
                    }

                    ForEach(node.children) { child in
                        NodeView(node: child)
                            .equatable()
                    }
                }
                .onDisappear {
                    if onDismissCb != 0 {
                        NativeElementBridge.sendPressEvent(onDismissCb, nodeId: nodeId)
                    }
                }
            }
    }
}
