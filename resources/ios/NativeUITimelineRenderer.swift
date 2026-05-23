import SwiftUI

struct NativeUITimelineRenderer: View {
    let node: NativeUINode

    private var isHorizontal: Bool {
        node.props.getString("orientation", default: "vertical") == "horizontal"
    }

    var body: some View {
        if isHorizontal {
            HStack(alignment: .top, spacing: 18) {
                ForEach(Array(node.children.enumerated()), id: \.element.id) { index, child in
                    timelineChild(child, isLast: index == node.children.count - 1, horizontal: true)
                }
            }
        } else {
            VStack(alignment: .leading, spacing: 0) {
                ForEach(Array(node.children.enumerated()), id: \.element.id) { index, child in
                    timelineChild(child, isLast: index == node.children.count - 1, horizontal: false)
                }
            }
        }
    }

    @ViewBuilder
    private func timelineChild(_ child: NativeUINode, isLast: Bool, horizontal: Bool) -> some View {
        if child.type == "timeline_block" {
            TimelineBlockBody(node: child, isLast: isLast, horizontal: horizontal)
        } else {
            NodeView(node: child).equatable()
        }
    }
}

struct NativeUITimelineBlockRenderer: View {
    let node: NativeUINode

    var body: some View {
        TimelineBlockBody(node: node, isLast: true, horizontal: false)
    }
}

private struct TimelineBlockBody: View {
    let node: NativeUINode
    let isLast: Bool
    let horizontal: Bool

    private var heading: String { node.props.getString("heading") }
    private var status: String { node.props.getString("status") }
    private var icon: String { node.props.getString("icon", default: "circle.fill") }

    var body: some View {
        if horizontal {
            VStack(alignment: .leading, spacing: 10) {
                HStack(alignment: .center, spacing: 8) {
                    marker
                    if !isLast {
                        Rectangle()
                            .fill(Color(argb: 0xFFE5E7EB))
                            .frame(width: 64, height: 2)
                    }
                }
                content
            }
        } else {
            HStack(alignment: .top, spacing: 12) {
                VStack(spacing: 8) {
                    marker
                    if !isLast {
                        Rectangle()
                            .fill(Color(argb: 0xFFE5E7EB))
                            .frame(width: 2, height: 72)
                    }
                }
                .frame(width: 30)

                content
                    .padding(.bottom, isLast ? 0 : 24)
            }
        }
    }

    private var marker: some View {
        ZStack {
            Circle()
                .fill(Color(argb: 0xFFF1F5F9))
                .frame(width: 28, height: 28)
            Image(systemName: getIconForName(icon))
                .font(.system(size: icon == "circle.fill" ? 9 : 15, weight: .semibold))
                .foregroundColor(Color(argb: 0xFF64748B))
        }
    }

    private var content: some View {
        VStack(alignment: .leading, spacing: 10) {
            if !heading.isEmpty || !status.isEmpty {
                HStack(alignment: .firstTextBaseline, spacing: 8) {
                    if !heading.isEmpty {
                        Text(heading)
                            .font(.system(size: 18, weight: .medium))
                            .foregroundColor(Color.primary)
                    }
                    if !status.isEmpty {
                        Text(status)
                            .font(.system(size: 12))
                            .foregroundColor(Color.secondary)
                    }
                }
            }

            ForEach(node.children) { child in
                NodeView(node: child).equatable()
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
