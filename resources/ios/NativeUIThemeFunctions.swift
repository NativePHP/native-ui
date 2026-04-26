import Foundation

// MARK: - NativeUI.Theme.* bridge functions
//
// PHP calls these via `nativephp_call('NativeUI.Theme.Set', jsonPayload)`.
// Registration is driven by the plugin manifest's `bridge_functions` block.

/// Namespace for theme-related bridge calls.
enum NativeUIThemeFunctions {

    /// `NativeUI.Theme.Set` — apply an effective token set to the runtime store.
    /// The `parameters` map is the decoded JSON from `Theme::all()` — light +
    /// dark color blocks, radii, and typography tokens.
    class Set: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            DispatchQueue.main.async {
                NativeUITheme.shared.apply(parameters)
            }
            return [:]
        }
    }
}