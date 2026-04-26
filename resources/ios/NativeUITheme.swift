import SwiftUI

// MARK: - Theme Tokens

/// One coherent set of theme tokens (light OR dark). Exposed to SwiftUI
/// renderers via the `\.nativeUITheme` environment value.
///
/// The PHP side (`Nativephp\NativeUi\Theme::merge([...])`) is the source of
/// truth. Tokens arrive over the bridge via `NativeUI.Theme.Set`.
struct NativeUITokens: Equatable {
    // Colors
    let primary: Color
    let onPrimary: Color
    let secondary: Color
    let onSecondary: Color
    let surface: Color
    let onSurface: Color
    let background: Color
    let onBackground: Color
    let surfaceVariant: Color
    let onSurfaceVariant: Color
    let outline: Color
    let destructive: Color
    let onDestructive: Color
    let accent: Color
    let onAccent: Color

    // Radii (points)
    let radiusSm: CGFloat
    let radiusMd: CGFloat
    let radiusLg: CGFloat
    let radiusFull: CGFloat

    // Typography
    let fontSm: CGFloat
    let fontMd: CGFloat
    let fontLg: CGFloat
    let fontXl: CGFloat
    let fontFamily: String

    /// Fallback matches `config/native-ui.php` defaults. Used before PHP has
    /// pushed an initial theme — keeps components renderable during bootstrap.
    static let fallback = NativeUITokens(
        primary:          Color(hex: "#0F766E"),
        onPrimary:        Color(hex: "#FFFFFF"),
        secondary:        Color(hex: "#64748B"),
        onSecondary:      Color(hex: "#FFFFFF"),
        surface:          Color(hex: "#FFFFFF"),
        onSurface:        Color(hex: "#0F172A"),
        background:       Color(hex: "#F8FAFC"),
        onBackground:     Color(hex: "#0F172A"),
        surfaceVariant:   Color(hex: "#F1F5F9"),
        onSurfaceVariant: Color(hex: "#475569"),
        outline:          Color(hex: "#CBD5E1"),
        destructive:      Color(hex: "#DC2626"),
        onDestructive:    Color(hex: "#FFFFFF"),
        accent:           Color(hex: "#FB923C"),
        onAccent:         Color(hex: "#FFFFFF"),
        radiusSm: 4, radiusMd: 8, radiusLg: 16, radiusFull: 9999,
        fontSm: 14, fontMd: 16, fontLg: 20, fontXl: 24,
        fontFamily: "System"
    )
}

// MARK: - Theme Store

/// Singleton holding the current effective theme tokens for both color schemes.
/// PHP pushes updates via `NativeUI.Theme.Set`; consumers pick the right scheme
/// via `resolve(for: colorScheme)`.
final class NativeUITheme: ObservableObject {
    static let shared = NativeUITheme()

    @Published private(set) var light: NativeUITokens = .fallback
    @Published private(set) var dark: NativeUITokens = .fallback

    private init() {}

    /// Pick the active token set based on the current system color scheme.
    func resolve(for scheme: ColorScheme) -> NativeUITokens {
        scheme == .dark ? dark : light
    }

    /// Called by the bridge on every `Theme::merge()` from PHP. Parameters are
    /// the full effective theme (light + dark + radii + fonts), post-merge
    /// and post-dark-auto-derivation.
    func apply(_ parameters: [String: Any]) {
        let lightMap = parameters["light"] as? [String: Any] ?? [:]
        let darkMap  = parameters["dark"]  as? [String: Any] ?? [:]

        let radiusSm   = cgf(parameters["radius-sm"],   fallback: light.radiusSm)
        let radiusMd   = cgf(parameters["radius-md"],   fallback: light.radiusMd)
        let radiusLg   = cgf(parameters["radius-lg"],   fallback: light.radiusLg)
        let radiusFull = cgf(parameters["radius-full"], fallback: light.radiusFull)

        let fontSm     = cgf(parameters["font-sm"],     fallback: light.fontSm)
        let fontMd     = cgf(parameters["font-md"],     fallback: light.fontMd)
        let fontLg     = cgf(parameters["font-lg"],     fallback: light.fontLg)
        let fontXl     = cgf(parameters["font-xl"],     fallback: light.fontXl)
        let fontFamily = (parameters["font-family"] as? String) ?? light.fontFamily

        func tokens(from map: [String: Any], fallbackTo: NativeUITokens) -> NativeUITokens {
            NativeUITokens(
                primary:          hex(map["primary"],            fallback: fallbackTo.primary),
                onPrimary:        hex(map["on-primary"],         fallback: fallbackTo.onPrimary),
                secondary:        hex(map["secondary"],          fallback: fallbackTo.secondary),
                onSecondary:      hex(map["on-secondary"],       fallback: fallbackTo.onSecondary),
                surface:          hex(map["surface"],            fallback: fallbackTo.surface),
                onSurface:        hex(map["on-surface"],         fallback: fallbackTo.onSurface),
                background:       hex(map["background"],         fallback: fallbackTo.background),
                onBackground:     hex(map["on-background"],      fallback: fallbackTo.onBackground),
                surfaceVariant:   hex(map["surface-variant"],    fallback: fallbackTo.surfaceVariant),
                onSurfaceVariant: hex(map["on-surface-variant"], fallback: fallbackTo.onSurfaceVariant),
                outline:          hex(map["outline"],            fallback: fallbackTo.outline),
                destructive:      hex(map["destructive"],        fallback: fallbackTo.destructive),
                onDestructive:    hex(map["on-destructive"],     fallback: fallbackTo.onDestructive),
                accent:           hex(map["accent"],             fallback: fallbackTo.accent),
                onAccent:         hex(map["on-accent"],          fallback: fallbackTo.onAccent),
                radiusSm: radiusSm, radiusMd: radiusMd, radiusLg: radiusLg, radiusFull: radiusFull,
                fontSm: fontSm, fontMd: fontMd, fontLg: fontLg, fontXl: fontXl,
                fontFamily: fontFamily
            )
        }

        let newLight = tokens(from: lightMap, fallbackTo: .fallback)
        let newDark  = tokens(from: darkMap,  fallbackTo: newLight)

        // `@Published` fires on every assignment — even when the new value
        // equals the old one. That would cause every observer (every Screen,
        // Button, TextInput…) to re-render on each bridge push, which PHP
        // sends on every service-provider boot (i.e. every request). Gate the
        // assignment on actual change to keep the observable stable.
        if newLight != self.light { self.light = newLight }
        if newDark  != self.dark  { self.dark  = newDark }
    }
}

// MARK: - Environment Propagation

private struct NativeUIThemeKey: EnvironmentKey {
    static let defaultValue: NativeUITokens = .fallback
}

extension EnvironmentValues {
    /// Active theme tokens for the current color scheme. Renderers read this
    /// instead of going through `NativeUITheme.shared` directly so SwiftUI can
    /// track dependencies and update components when tokens change.
    var nativeUITheme: NativeUITokens {
        get { self[NativeUIThemeKey.self] }
        set { self[NativeUIThemeKey.self] = newValue }
    }
}

// MARK: - Parsing helpers

private func hex(_ any: Any?, fallback: Color) -> Color {
    guard let s = any as? String, s.hasPrefix("#") else { return fallback }
    return Color(hex: s)
}

private func cgf(_ any: Any?, fallback: CGFloat) -> CGFloat {
    if let n = any as? CGFloat { return n }
    if let n = any as? Double { return CGFloat(n) }
    if let n = any as? Int { return CGFloat(n) }
    if let n = any as? NSNumber { return CGFloat(truncating: n) }
    return fallback
}

extension Color {
    /// Construct from `#RRGGBB` or `#AARRGGBB` hex string. Falls back to black
    /// on malformed input.
    init(hex: String) {
        var s = hex
        if s.hasPrefix("#") { s.removeFirst() }
        s = s.uppercased()

        var argb: UInt64 = 0xFF000000
        if s.count == 6, let rgb = UInt64(s, radix: 16) {
            argb = 0xFF000000 | rgb
        } else if s.count == 8, let full = UInt64(s, radix: 16) {
            argb = full
        }

        let a = Double((argb >> 24) & 0xFF) / 255.0
        let r = Double((argb >> 16) & 0xFF) / 255.0
        let g = Double((argb >>  8) & 0xFF) / 255.0
        let b = Double((argb >>  0) & 0xFF) / 255.0
        self = Color(red: r, green: g, blue: b, opacity: a)
    }
}
