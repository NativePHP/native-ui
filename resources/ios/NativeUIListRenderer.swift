import SwiftUI

/// A decoded swipe-action descriptor (one button on either edge).
private struct SwipeActionSpec: Decodable, Identifiable {
    let cb: Int
    let label: String
    let icon: String
    let tint: String
    let role: String

    var id: Int { cb }
}

private func decodeActions(_ json: String) -> [SwipeActionSpec] {
    guard !json.isEmpty, let data = json.data(using: .utf8) else { return [] }
    return (try? JSONDecoder().decode([SwipeActionSpec].self, from: data)) ?? []
}

private func colorFromHex(_ hex: String) -> Color? {
    let s = hex.trimmingCharacters(in: .whitespaces).replacingOccurrences(of: "#", with: "")
    guard s.count == 6, let v = UInt32(s, radix: 16) else { return nil }
    let r = Double((v >> 16) & 0xFF) / 255.0
    let g = Double((v >> 8) & 0xFF) / 255.0
    let b = Double(v & 0xFF) / 255.0
    return Color(.sRGB, red: r, green: g, blue: b, opacity: 1)
}

struct NativeUIListRenderer: View {
    let node: NativeUINode

    var body: some View {
        let horizontal = node.props.getBool("horizontal")
        let separator = node.props.getBool("separator")
        let onRefreshCb = node.props.getCallbackId("on_refresh")
        let onEndReachedCb = node.props.getCallbackId("on_end_reached")
        let nodeId = node.id
        let children = node.children

        if horizontal {
            ScrollView(.horizontal) {
                LazyHStack(spacing: 0) {
                    ForEach(children) { child in
                        NodeView(node: child)
                            .equatable()
                    }
                }
            }
        } else {
            List {
                ForEach(Array(children.enumerated()), id: \.element.id) { index, child in
                    // Legacy single-action API.
                    let legacyDeleteCb = child.props.getCallbackId("on_swipe_delete")
                    // New multi-action API.
                    let leading = decodeActions(child.props.getString("leading_actions_json", default: ""))
                    let trailing = decodeActions(child.props.getString("trailing_actions_json", default: ""))

                    NodeView(node: child)
                        .equatable()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .listRowInsets(EdgeInsets())
                        .listRowSeparator(separator ? .visible : .hidden)
                        .swipeActions(edge: .leading, allowsFullSwipe: false) {
                            ForEach(leading) { action in
                                actionButton(spec: action, nodeId: child.id)
                            }
                        }
                        .swipeActions(edge: .trailing, allowsFullSwipe: trailing.contains(where: { $0.role == "destructive" }) || legacyDeleteCb != 0) {
                            // New multi-action takes precedence over legacy.
                            if !trailing.isEmpty {
                                ForEach(trailing) { action in
                                    actionButton(spec: action, nodeId: child.id)
                                }
                            } else if legacyDeleteCb != 0 {
                                Button(role: .destructive) {
                                    NativeElementBridge.sendPressEvent(legacyDeleteCb, nodeId: child.id)
                                } label: {
                                    Label("Delete", systemImage: "trash")
                                }
                            }
                        }
                        .onAppear {
                            if onEndReachedCb != 0 && index >= children.count - 3 {
                                NativeElementBridge.sendPressEvent(onEndReachedCb, nodeId: nodeId)
                            }
                        }
                }
            }
            .listStyle(.plain)
            .scrollDismissesKeyboard(.interactively)
            .refreshable {
                if onRefreshCb != 0 {
                    NativeElementBridge.sendPressEvent(onRefreshCb, nodeId: nodeId)
                    try? await Task.sleep(nanoseconds: 1_000_000_000)
                }
            }
        }
    }

    /// Build a SwiftUI Button for one swipe action spec.
    /// Destructive role gets the red treatment automatically; otherwise
    /// the configured tint (if any) wins.
    @ViewBuilder
    private func actionButton(spec: SwipeActionSpec, nodeId: Int) -> some View {
        let role: ButtonRole? = spec.role == "destructive" ? .destructive : nil
        let button = Button(role: role) {
            NativeElementBridge.sendPressEvent(spec.cb, nodeId: nodeId)
        } label: {
            if !spec.icon.isEmpty {
                Label(spec.label.isEmpty ? " " : spec.label, systemImage: spec.icon)
            } else {
                Text(spec.label.isEmpty ? " " : spec.label)
            }
        }

        if role != .destructive, let tint = colorFromHex(spec.tint) {
            button.tint(tint)
        } else {
            button
        }
    }
}
