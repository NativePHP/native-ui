package com.nativephp.plugins.native_ui

import android.content.Context
import com.nativephp.mobile.ui.nativerender.NativeRootHostRegistry
import com.nativephp.plugins.native_ui.ui.NativeLayoutDrawerHost

/**
 * Init function invoked by the generated `PluginBridgeFunctionRegistration` in
 * `onCreate` (before the first composition). Registers native-ui's root-host
 * chrome on core's `NativeRootHostRegistry`. Declared in the plugin manifest
 * under `android.init_function`.
 *
 * The `context` parameter is supplied by the generated call site (`registerNativeUIChrome(context)`);
 * the drawer host needs none of it.
 */
@Suppress("UNUSED_PARAMETER")
fun registerNativeUIChrome(context: Context) {
    NativeRootHostRegistry.register("native-ui.drawer", consumes = "native_drawer") { root, content ->
        val drawerNode = root.children.firstOrNull { it.type == "native_drawer" }
        NativeLayoutDrawerHost(drawerNode = drawerNode, content = content)
    }
}
