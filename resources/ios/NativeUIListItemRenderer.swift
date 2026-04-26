import SwiftUI

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

            // Trailing content
            buildTrailingContent(
                type: trailingType.isEmpty ? (trailingIcon.isEmpty ? "" : "icon") : trailingType,
                value: trailingValue.isEmpty ? trailingIcon : trailingValue,
                iconColor: trailingIconColor,
                textColor: trailingTextColor
            )
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
