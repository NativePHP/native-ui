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
}

// The `Edge\Transition` → SwiftUI `AnyTransition` mapping that ContentView uses
// now lives in core (`nativeScreenTransition(for:)`), so core can swap native
// trees without depending on this plugin. This file keeps only the
// `NativeUI.Transition.Set` bridge function, which stages the value PHP sends.
