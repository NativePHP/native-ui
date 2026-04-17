import SwiftUI

struct ComposeUIListRenderer: View {
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
            // Use native List for reliable pull-to-refresh support
            List {
                ForEach(Array(children.enumerated()), id: \.element.id) { index, child in
                    let deleteCb = child.props.getCallbackId("on_swipe_delete")

                    NodeView(node: child)
                        .equatable()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .listRowInsets(EdgeInsets())
                        .listRowSeparator(separator ? .visible : .hidden)
                        .swipeActions(edge: .trailing, allowsFullSwipe: true) {
                            if deleteCb != 0 {
                                Button(role: .destructive) {
                                    NativeElementBridge.sendPressEvent(deleteCb, nodeId: child.id)
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
}
