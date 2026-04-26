<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

/**
 * Full-bleed themed container — applies `theme.background` as the backdrop
 * and `theme.onBackground` as the default content color for descendants.
 *
 * Intended as the root of a screen:
 *
 *     <native:screen>
 *         <native:scroll-view>
 *             ...page content...
 *         </native:scroll-view>
 *     </native:screen>
 *
 * Background adapts automatically to the system's light/dark mode — the
 * renderer resolves the appropriate token on each recomposition. No props
 * needed; it's a contextual backdrop, not a styled element.
 */
class Screen extends Element
{
    protected string $type = 'screen';

    public static function make(): static
    {
        return new static;
    }

    public function applyAttributes(array $attrs): void
    {
        // Screen is props-less — it's a themed backdrop. Attribute slot is
        // kept open so `applyLayout()` etc. can still add margins/width
        // without fighting the renderer.
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        return [];
    }
}