<?php

namespace Nativephp\NativeUi\Concerns;

use Nativephp\NativeUi\Builders\Drawer;

/**
 * Per-screen control over the layout's side drawer. `use` this trait on a
 * `NativeComponent` screen to override or suppress the drawer its layout
 * declares via {@see HasLayoutDrawer}.
 *
 *   class AdminScreen extends NativeComponent
 *   {
 *       use InteractsWithDrawer;
 *
 *       // Replace the layout drawer just for this screen:
 *       public function drawerOverride(): ?Drawer
 *       {
 *           return Drawer::make(view('native.admin-side_bar'))->reveal();
 *       }
 *   }
 *
 * Mirrors how `navigationOptions()` overrides the layout nav bar. The native-ui
 * chrome contributor checks these (via `method_exists`) before falling back to
 * the layout.
 */
trait InteractsWithDrawer
{
    /**
     * Suppress the layout's drawer on this screen. Default `false`
     * → the layout's `drawer()` (if any) shows.
     */
    protected bool $hidesDrawer = false;

    /**
     * Override to provide a per-screen drawer that wins over the layout's
     * `drawer()`. Returning null falls back to the layout.
     */
    public function drawerOverride(): ?Drawer
    {
        return null;
    }

    /** Whether this screen suppresses its layout's drawer. */
    public function hidesDrawer(): bool
    {
        return $this->hidesDrawer;
    }
}
