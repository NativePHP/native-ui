package com.nativephp.plugins.native_ui

import androidx.fragment.app.FragmentActivity
import com.nativephp.mobile.bridge.BridgeFunction
import com.nativephp.mobile.ui.nativerender.NativeUIBridge

/**
 * NativeUI.Transition.* bridge functions.
 *
 * PHP signals an inter-screen transition just before publishing the next
 * element tree:
 *
 *   nativephp_call('NativeUI.Transition.Set', json_encode(['type' => 'slide_from_right']));
 *
 * The Set handler stages `pendingTransition` + `navigationPending` on the
 * shared NativeUIBridge. The next postTreeUpdate flips `screenKey`, and
 * Compose's tree renderer transitions the swap accordingly.
 *
 * Recognised types: slide_from_right, slide_from_left, slide_from_bottom,
 * fade, fade_from_bottom, scale_from_center, none. Unknown values fall
 * back to the default crossfade in the renderer.
 *
 * Registration is driven by the plugin manifest's `bridge_functions` block.
 */
object NativeUITransitionFunctions {

    /**
     * `NativeUI.Transition.Set` — stage a transition for the next published tree.
     *
     * The activity arg isn't used today (state lives on a singleton in
     * NativeUIBridge), but the AndroidPluginCompiler always emits
     * `Set(activity)` for consistency with other bridge functions.
     */
    @Suppress("UNUSED_PARAMETER")
    class Set(activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            val type = parameters["type"] as? String ?: "fade"
            NativeUIBridge.setNavigationPending(type)
            return mapOf("success" to true)
        }
    }
}
