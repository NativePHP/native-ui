import SwiftUI

struct ComposeUIBottomSheetRenderer: View {
    let node: NativeUINode
    @State private var isPresented: Bool = false

    var body: some View {
        let visible = node.props.getBool("visible")
        let onDismissCb = node.props.getCallbackId("on_dismiss")
        let detentsStr = node.props.getString("detents", default: "medium,large")
        let bgColorArgb = node.props.getColor("background_color", default: 0)

        Color.clear.frame(width: 0, height: 0)
            .sheet(isPresented: $isPresented, onDismiss: {
                if onDismissCb != 0 {
                    NativeUIBridge.sendSheetDismissEvent(onDismissCb, nodeId: node.id)
                }
            }) {
                VStack(spacing: 0) {
                    ForEach(node.children) { child in
                        RenderNode(node: child)
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(bgColorArgb != 0 ? Color(argb: bgColorArgb) : Color(uiColor: .systemBackground))
                .presentationDetents(resolveDetents(detentsStr))
                .presentationDragIndicator(.visible)
            }
            .onAppear { isPresented = visible }
            .onChange(of: visible) { _, v in isPresented = v }
    }

    private func resolveDetents(_ str: String) -> Set<PresentationDetent> {
        let parts = str.split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces).lowercased() }
        var detents = Set<PresentationDetent>()
        for part in parts {
            switch part {
            case "small": detents.insert(.fraction(0.25))
            case "medium": detents.insert(.medium)
            case "large": detents.insert(.large)
            case "full": detents.insert(.fraction(1.0))
            default:
                if let fraction = Double(part), fraction > 0, fraction <= 1 {
                    detents.insert(.fraction(CGFloat(fraction)))
                }
            }
        }
        return detents.isEmpty ? [.medium, .large] : detents
    }
}
