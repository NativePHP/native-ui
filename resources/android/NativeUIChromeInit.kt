package com.nativephp.plugins.native_ui

import android.content.Context
import com.nativephp.mobile.ui.NativeUIThemeProvider
import com.nativephp.mobile.ui.nativerender.NativeRootHostRegistry
import com.nativephp.plugins.native_ui.ui.NativeLayoutDrawerHost

/**
 * Init function invoked by the generated `PluginBridgeFunctionRegistration` in
 * `onCreate` (before the first composition). Wires native-ui into core's seams:
 * registers the layout-drawer root host, and supplies the app's Material3
 * ColorScheme from native-ui's PHP-driven theme tokens. Declared in the plugin
 * manifest under `android.init_function`.
 *
 * The `context` parameter is supplied by the generated call site
 * (`registerNativeUIChrome(context)`); these registrations need none of it.
 */
@Suppress("UNUSED_PARAMETER")
fun registerNativeUIChrome(context: Context) {
    NativeRootHostRegistry.register("native-ui.drawer", consumes = "native_drawer") { root, content ->
        val drawerNode = root.children.firstOrNull { it.type == "native_drawer" }
        NativeLayoutDrawerHost(drawerNode = drawerNode, content = content)
    }

    // Supply the app's color scheme from native-ui's theme tokens. The lambda
    // reads NativeUITheme.{light,dark} (Compose snapshot state) when invoked
    // during composition, so PHP-side Theme::merge updates stay reactive.
    NativeUIThemeProvider.colorSchemeFor = { isDark ->
        (if (isDark) NativeUITheme.dark else NativeUITheme.light).toMaterialColorScheme(isDark)
    }
}
