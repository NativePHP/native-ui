import SwiftUI

/// One status badge in a list-item's trailing stack.
private struct ListItemBadgeSpec: Decodable, Identifiable {
    let icon: String
    let icon_variant: String
    let color: String

    var id: String { icon + ":" + color }
}

private func decodeListItemBadges(_ json: String) -> [ListItemBadgeSpec] {
    guard !json.isEmpty, let data = json.data(using: .utf8) else { return [] }
    return (try? JSONDecoder().decode([ListItemBadgeSpec].self, from: data)) ?? []
}

/// Parse `#RRGGBB` to a SwiftUI Color. Returns nil for empty / invalid.
private func parseListItemHex(_ hex: String) -> Color? {
    let s = hex.trimmingCharacters(in: .whitespaces).replacingOccurrences(of: "#", with: "")
    guard s.count == 6, let v = UInt32(s, radix: 16) else { return nil }
    let r = Double((v >> 16) & 0xFF) / 255.0
    let g = Double((v >> 8) & 0xFF) / 255.0
    let b = Double(v & 0xFF) / 255.0
    return Color(.sRGB, red: r, green: g, blue: b, opacity: 1)
}

struct NativeUIListItemRenderer: View {
    let node: NativeUINode

    var body: some View {
        let p = node.props
        let headline = p.getString("headline")
        let supporting = p.getString("supporting")
        let overline = p.getString("overline")
        let disabled = p.getBool("disabled")

        // Colors
        let headlineColor = p.getColor("headline_color", default: 0)
        let supportingColor = p.getColor("supporting_color", default: 0)
        let overlineColor = p.getColor("overline_color", default: 0)
        let containerColor = p.getColor("container_color", default: 0)

        // Leading content
        let leadingType = p.getString("leading_type")
        let leadingValue = p.getString("leading_value")
        let leadingIcon = p.getString("leading_icon")
        let leadingMonogramColor = p.getColor("leading_monogram_color", default: 0)

        // Trailing content
        let trailingType = p.getString("trailing_type")
        let trailingValue = p.getString("trailing_value")
        let trailingIcon = p.getString("trailing_icon")
        let trailingTextColor = p.getColor("trailing_text_color", default: 0)
        let trailingIconColor = p.getColor("trailing_icon_color", default: 0)

        HStack(spacing: 16) {
            // Leading content
            buildLeadingContent(
                type: leadingType.isEmpty ? (leadingIcon.isEmpty ? "" : "icon") : leadingType,
                value: leadingValue.isEmpty ? leadingIcon : leadingValue,
                monogramColor: leadingMonogramColor
            )

            // Text content
            VStack(alignment: .leading, spacing: 2) {
                if !overline.isEmpty {
                    Text(overline)
                        .font(.caption)
                        .foregroundColor(overlineColor != 0 ? Color(argb: overlineColor) : .secondary)
                }
                Text(headline)
                    .font(.body)
                    .foregroundColor(headlineColor != 0 ? Color(argb: headlineColor) : .primary)
                if !supporting.isEmpty {
                    Text(supporting)
                        .font(.subheadline)
                        .foregroundColor(supportingColor != 0 ? Color(argb: supportingColor) : .secondary)
                }
            }

            Spacer()

            // Trailing — multi-badge stack wins over single trailingIcon.
            let badgesJson = p.getString("trailing_badges_json", default: "")
            if !badgesJson.isEmpty {
                buildTrailingBadges(json: badgesJson)
            } else {
                buildTrailingContent(
                    type: trailingType.isEmpty ? (trailingIcon.isEmpty ? "" : "icon") : trailingType,
                    value: trailingValue.isEmpty ? trailingIcon : trailingValue,
                    iconColor: trailingIconColor,
                    textColor: trailingTextColor
                )
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(containerColor != 0 ? Color(argb: containerColor) : Color.clear)
        .opacity(disabled ? 0.5 : 1.0)
        .applyClickHandlers(node: node)
    }

    @ViewBuilder
    private func buildLeadingContent(type: String, value: String, monogramColor: Int) -> some View {
        switch type {
        case "icon":
            Image(systemName: getIconForName(value))
                .frame(width: 24, height: 24)
                .foregroundColor(.secondary)
        case "avatar":
            AsyncImage(url: URL(string: value)) { image in
                image.resizable().scaledToFill()
            } placeholder: {
                Circle().fill(Color(.systemGray5))
            }
            .frame(width: 40, height: 40)
            .clipShape(Circle())
        case "monogram":
            let bgColor = monogramColor != 0 ? Color(argb: monogramColor) : Color.accentColor
            ZStack {
                Circle().fill(bgColor)
                Text(String(value.prefix(2)).uppercased())
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(.white)
            }
            .frame(width: 40, height: 40)
        case "image":
            AsyncImage(url: URL(string: value)) { image in
                image.resizable().scaledToFill()
            } placeholder: {
                RoundedRectangle(cornerRadius: 4).fill(Color(.systemGray5))
            }
            .frame(width: 56, height: 56)
            .clipShape(RoundedRectangle(cornerRadius: 4))
        case "checkbox":
            Image(systemName: "square")
                .foregroundColor(.secondary)
        case "radio":
            Image(systemName: "circle")
                .foregroundColor(.secondary)
        default:
            EmptyView()
        }
    }

    /// Decode + render a horizontal stack of small status badges (e.g.
    /// a flag + a pin both visible). Wins over the single
    /// `trailing_icon` slot when present.
    @ViewBuilder
    private func buildTrailingBadges(json: String) -> some View {
        let badges = decodeListItemBadges(json)
        if !badges.isEmpty {
            HStack(spacing: 6) {
                ForEach(badges) { b in
                    Image(systemName: getIconForName(b.icon))
                        .font(.system(size: 15))
                        .foregroundColor(parseListItemHex(b.color) ?? .secondary)
                }
            }
        } else {
            EmptyView()
        }
    }

    @ViewBuilder
    private func buildTrailingContent(type: String, value: String, iconColor: Int, textColor: Int) -> some View {
        switch type {
        case "icon":
            Image(systemName: getIconForName(value))
                .frame(width: 24, height: 24)
                .foregroundColor(iconColor != 0 ? Color(argb: iconColor) : .secondary)
        case "text":
            Text(value)
                .foregroundColor(textColor != 0 ? Color(argb: textColor) : .secondary)
        case "icon_button":
            // When the row has `:trailing-menu` attached, the trailing
            // icon button becomes a Menu trigger instead of a plain
            // press. SwiftUI Menu absorbs the tap to open the dropdown,
            // so the on_trailing_press handler is naturally shadowed
            // (matches the spec — menu wins).
            if node.props.getBool("has_trailing_menu") {
                let menuItems = node.children.filter { $0.type == "top_bar_action" }
                Menu {
                    ForEach(menuItems) { item in
                        listItemMenuItem(item)
                    }
                } label: {
                    // `.contentShape(Rectangle())` expands the tap target
                    // to the full 24×24 frame; without it only the
                    // symbol's opaque pixels respond, which makes the
                    // ellipsis glyph (mostly empty space) almost
                    // un-tappable.
                    Image(systemName: getIconForName(value))
                        .frame(width: 24, height: 24)
                        .foregroundColor(iconColor != 0 ? Color(argb: iconColor) : .secondary)
                        .contentShape(Rectangle())
                }
            } else {
                Button(action: {
                    let onPressCb = node.props.getCallbackId("on_trailing_press")
                    if onPressCb != 0 {
                        NativeUIBridge.sendPressEvent(onPressCb, nodeId: node.id)
                    }
                }) {
                    Image(systemName: getIconForName(value))
                        .frame(width: 24, height: 24)
                        .foregroundColor(iconColor != 0 ? Color(argb: iconColor) : .secondary)
                }
            }
        case "switch":
            EmptyView() // Switch requires state management - handled at a higher level
        case "checkbox":
            Image(systemName: "square")
                .foregroundColor(.secondary)
        default:
            EmptyView()
        }
    }
}

/// Render one menu item attached via `:trailing-menu`. Same shape as
/// `pressableMenuItem` / `buttonMenuItem`.
@ViewBuilder
private func listItemMenuItem(_ item: NativeUINode) -> some View {
    if item.props.getBool("divider") {
        Divider()
    } else {
        let label = item.props.getString("label", default: "")
        let icon = item.props.getString("icon", default: "")
        let isDestructive = item.props.getBool("destructive")
        Button(role: isDestructive ? .destructive : nil) {
            if item.onPress != 0 {
                NativeUIBridge.sendPressEvent(item.onPress, nodeId: item.id)
            }
        } label: {
            if !icon.isEmpty {
                Label(label, systemImage: getIconForName(icon))
            } else {
                Text(label)
            }
        }
        .tint(isDestructive ? .red : nil)
    }
}
