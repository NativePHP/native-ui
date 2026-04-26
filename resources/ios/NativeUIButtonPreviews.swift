import SwiftUI

// SwiftUI #Preview harnesses for NativeUIButtonRenderer.
//
// Lets you iterate on Button visuals in Xcode's preview canvas without
// going through a full PHP render cycle. Previews use the fallback theme
// (matches the plugin's config defaults).

private func mockNode(_ props: [String: Any]) -> NativeUINode {
    NativeUINode(
        id: 1,
        type: "button",
        layout: nil,
        style: nil,
        props: GenericProps(props),
        onPress: 0,
        onLongPress: 0,
        children: []
    )
}

#Preview("Primary · md") {
    NativeUIButtonRenderer(node: mockNode([
        "variant": "primary",
        "label": "Save changes",
    ]))
    .environment(\.nativeUITheme, .fallback)
    .padding()
}

#Preview("Primary · sm") {
    NativeUIButtonRenderer(node: mockNode([
        "variant": "primary",
        "size": "sm",
        "label": "Save",
    ]))
    .environment(\.nativeUITheme, .fallback)
    .padding()
}

#Preview("Primary · lg") {
    NativeUIButtonRenderer(node: mockNode([
        "variant": "primary",
        "size": "lg",
        "label": "Get started",
    ]))
    .environment(\.nativeUITheme, .fallback)
    .padding()
}

#Preview("Primary · with leading icon") {
    NativeUIButtonRenderer(node: mockNode([
        "variant": "primary",
        "icon": "plus",
        "label": "Add item",
    ]))
    .environment(\.nativeUITheme, .fallback)
    .padding()
}

#Preview("Secondary") {
    NativeUIButtonRenderer(node: mockNode([
        "variant": "secondary",
        "label": "Cancel",
    ]))
    .environment(\.nativeUITheme, .fallback)
    .padding()
}

#Preview("Destructive") {
    NativeUIButtonRenderer(node: mockNode([
        "variant": "destructive",
        "icon": "trash",
        "label": "Delete",
    ]))
    .environment(\.nativeUITheme, .fallback)
    .padding()
}

#Preview("Ghost") {
    NativeUIButtonRenderer(node: mockNode([
        "variant": "ghost",
        "label": "Learn more",
    ]))
    .environment(\.nativeUITheme, .fallback)
    .padding()
}

#Preview("Disabled") {
    NativeUIButtonRenderer(node: mockNode([
        "variant": "primary",
        "disabled": true,
        "label": "Unavailable",
    ]))
    .environment(\.nativeUITheme, .fallback)
    .padding()
}

#Preview("Loading") {
    NativeUIButtonRenderer(node: mockNode([
        "variant": "primary",
        "loading": true,
        "label": "Saving…",
    ]))
    .environment(\.nativeUITheme, .fallback)
    .padding()
}

#Preview("All variants") {
    VStack(spacing: 12) {
        NativeUIButtonRenderer(node: mockNode(["variant": "primary",     "label": "Primary"]))
        NativeUIButtonRenderer(node: mockNode(["variant": "secondary",   "label": "Secondary"]))
        NativeUIButtonRenderer(node: mockNode(["variant": "destructive", "label": "Destructive"]))
        NativeUIButtonRenderer(node: mockNode(["variant": "ghost",       "label": "Ghost"]))
    }
    .environment(\.nativeUITheme, .fallback)
    .padding()
}