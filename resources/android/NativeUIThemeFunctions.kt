package com.nativephp.plugins.native_ui

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import com.nativephp.mobile.bridge.BridgeFunction

/**
 * NativeUI.Theme.* bridge functions.
 *
 * PHP calls these via `nativephp_call('NativeUI.Theme.Set', jsonPayload)`.
 * Registration is driven by the plugin manifest's `bridge_functions` block.
 */
object NativeUIThemeFunctions {

    /**
     * `NativeUI.Theme.Set` — apply an effective token set to the runtime store.
     * The [parameters] map is the decoded JSON from `Theme::all()` — light +
     * dark color blocks, radii, and typography tokens.
     */
    class Set(private val context: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            Handler(Looper.getMainLooper()).post {
                NativeUITheme.apply(parameters)
            }
            return emptyMap()
        }
    }
}