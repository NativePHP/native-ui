import SwiftUI
import UIKit

struct ComposeUITextInputRenderer: View {
    let node: NativeUINode
    @State private var text: String = ""

    var body: some View {
        let p = node.props
        let placeholder = p.getString("placeholder")
        let label = p.getString("label")
        let onChangeCb = p.getCallbackId("on_change")
        let onSubmitCb = p.getCallbackId("on_submit")
        let secure = p.getBool("secure")
        let multiline = p.getBool("multiline")
        let kbType = resolveKeyboardType(p.getInt("keyboard"))
        let disabled = p.getBool("disabled")
        let isError = p.getBool("is_error")
        let maxLength = p.getInt("max_length")
        let maxLines = p.getInt("max_lines")
        let prefix = p.getString("prefix")
        let suffix = p.getString("suffix")
        let supporting = p.getString("supporting")
        let leadingIcon = p.getString("leading_icon")
        let trailingIcon = p.getString("trailing_icon")
        let fontSize = p.getFloat("font_size")
        let fontWeightInt = p.getInt("font_weight")
        let textColorInt = p.getColor("text_color", default: 0)
        let accentColorInt = p.getColor("color", default: 0)

        VStack(alignment: .leading, spacing: 4) {
            // Label
            if !label.isEmpty {
                Text(label)
                    .font(.caption)
                    .foregroundColor(isError ? .red : (accentColorInt != 0 ? Color(argb: accentColorInt) : .secondary))
            }

            HStack(spacing: 8) {
                // Leading icon
                if !leadingIcon.isEmpty {
                    Image(systemName: getIconForName(leadingIcon))
                        .foregroundColor(.secondary)
                        .frame(width: 20, height: 20)
                }

                // Prefix
                if !prefix.isEmpty {
                    Text(prefix)
                        .foregroundColor(.secondary)
                }

                // Text field
                Group {
                    if secure {
                        SecureField(placeholder, text: $text)
                    } else if multiline {
                        TextField(placeholder, text: $text, axis: .vertical)
                            .keyboardType(kbType)
                            .lineLimit(maxLines > 0 ? maxLines : 5)
                    } else {
                        TextField(placeholder, text: $text)
                            .keyboardType(kbType)
                    }
                }
                .font(.system(
                    size: fontSize > 0 ? CGFloat(fontSize) : 16,
                    weight: resolveFontWeight(fontWeightInt)
                ))
                .foregroundColor(textColorInt != 0 ? Color(argb: textColorInt) : .primary)
                .disabled(disabled)
                .tint(accentColorInt != 0 ? Color(argb: accentColorInt) : nil)
                .submitLabel(onSubmitCb != 0 ? .done : .return)
                .onSubmit {
                    if onSubmitCb != 0 {
                        NativeUIBridge.sendSubmitEvent(onSubmitCb, nodeId: node.id, text: text)
                    }
                }
                .onChange(of: text) { _, newValue in
                    let filtered = maxLength > 0 ? String(newValue.prefix(maxLength)) : newValue
                    if filtered != newValue { text = filtered }
                    if onChangeCb != 0 {
                        NativeUIBridge.sendTextChangeEvent(onChangeCb, nodeId: node.id, text: filtered)
                    }
                }

                // Suffix
                if !suffix.isEmpty {
                    Text(suffix)
                        .foregroundColor(.secondary)
                }

                // Trailing icon
                if !trailingIcon.isEmpty {
                    Image(systemName: getIconForName(trailingIcon))
                        .foregroundColor(.secondary)
                        .frame(width: 20, height: 20)
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .background(Color(.systemGray6))
            .cornerRadius(8)
            .overlay(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(isError ? Color.red : Color.clear, lineWidth: 1)
            )

            // Supporting text
            if !supporting.isEmpty {
                Text(supporting)
                    .font(.caption)
                    .foregroundColor(isError ? .red : .secondary)
            }
        }
        .onAppear {
            text = node.props.getString("value")
        }
    }

    private func resolveKeyboardType(_ type: Int) -> UIKeyboardType {
        switch type {
        case 1: return .numberPad
        case 2: return .emailAddress
        case 3: return .phonePad
        case 4: return .URL
        case 5: return .decimalPad
        case 6: return .numberPad
        default: return .default
        }
    }

    private func resolveFontWeight(_ weight: Int) -> Font.Weight {
        switch weight {
        case 1: return .thin
        case 2: return .light
        case 3: return .regular
        case 4: return .medium
        case 5: return .semibold
        case 6: return .bold
        case 7: return .heavy
        default: return .regular
        }
    }
}
