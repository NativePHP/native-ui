import Foundation
import SwiftUI

// MARK: - NativeUI.Transition.* bridge functions
//
// PHP signals an inter-screen transition via:
//   nativephp_call('NativeUI.Transition.Set', json_encode(['type' => 'slide_from_right']));
//
// The Set handler stages `pendingTransition` + `navigationPending` on the
// shared NativeUIBridge. The next nativephp_element_publish() flips
// `screenKey`, which causes SwiftUI to remount the tree renderer with
// the staged transition.

enum NativeUITransitionFunctions {

    /// `NativeUI.Transition.Set` — stage a transition for the next published tree.
    class Set: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            let type = (parameters["type"] as? String) ?? "fade"

            if Thread.isMainThread {
                NativeUIBridge.shared.setNavigationPending(transition: type)
            } else {
                DispatchQueue.main.sync {
                    NativeUIBridge.shared.setNavigationPending(transition: type)
                }
            }

            return ["success": true]
        }
    }

    /// Map a PHP-side Edge\Transition value to a SwiftUI AnyTransition.
    /// ContentView (or any host that swaps native trees) calls this with
    /// the current `NativeUIBridge.shared.pendingTransition` value.
    ///
    /// Recognised: slide_from_right, slide_from_left, slide_from_bottom,
    /// fade, fade_from_bottom, scale_from_center, none. Unknown values
    /// fall back to opacity.
    static func transition(for type: String?) -> AnyTransition {
        switch type {
        case "slide_from_right":
            return .asymmetric(
                insertion: .move(edge: .trailing),
                removal:   .move(edge: .leading)
            )
        case "slide_from_left":
            return .asymmetric(
                insertion: .move(edge: .leading),
                removal:   .move(edge: .trailing)
            )
        case "slide_from_bottom":
            return .move(edge: .bottom)
        case "fade":
            return .opacity
        case "fade_from_bottom":
            return .move(edge: .bottom).combined(with: .opacity)
        case "scale_from_center":
            return .scale.combined(with: .opacity)
        case "none":
            return .identity
        default:
            return .opacity
        }
    }
}
