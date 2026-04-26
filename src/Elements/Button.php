<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

/**
 * Native button.
 *
 * Renders as the platform's native button primitive — Material3 `Button`
 * family on Android, SwiftUI `Button` with `buttonStyle(...)` on iOS.
 *
 * API shape (locked in plan doc, sections A/C/E):
 *   - `variant`: semantic vocabulary (primary | secondary | destructive | ghost)
 *   - `size`: sm | md | lg
 *   - `disabled`, `loading`: state
 *   - `icon`, `icon-trailing`: optional icon names (leading/trailing)
 *   - `a11y-label`, `a11y-hint`: accessibility overrides
 *   - `@press`: tap callback
 *
 * Label/content comes from the Blade slot, not a prop — see
 * `Components\Button` for slot capture.
 *
 * Per Model 3 customization (theme-only), there is intentionally NO per-instance
 * color, background, border, radius, shadow, font-size, or font-weight. All
 * visual styling comes from the theme (`Nativephp\NativeUi\Theme`). For full
 * visual control, drop to `<native:pressable>` with your own content.
 */
class Button extends Element
{
    protected string $type = 'button';

    /** @var array<string, mixed> */
    protected array $buttonProps = [];

    protected ?string $pressCallback = null;

    public static function make(string $label = ''): static
    {
        $el = new static;

        if ($label !== '') {
            $el->buttonProps['label'] = $label;
        }

        return $el;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['label'])) {
            $this->buttonProps['label'] = $attrs['label'];
        }
        if (isset($attrs['variant'])) {
            $this->variant($attrs['variant']);
        }
        if (isset($attrs['size'])) {
            $this->size($attrs['size']);
        }
        if (! empty($attrs['disabled'])) {
            $this->disabled();
        }
        if (! empty($attrs['loading'])) {
            $this->loading();
        }
        if (isset($attrs['icon'])) {
            $this->icon($attrs['icon']);
        }
        if (isset($attrs['icon-trailing']) || isset($attrs['iconTrailing'])) {
            $this->iconTrailing($attrs['icon-trailing'] ?? $attrs['iconTrailing']);
        }
        if (isset($attrs['a11y-label']) || isset($attrs['a11yLabel'])) {
            $this->a11yLabel($attrs['a11y-label'] ?? $attrs['a11yLabel']);
        }
        if (isset($attrs['a11y-hint']) || isset($attrs['a11yHint'])) {
            $this->a11yHint($attrs['a11y-hint'] ?? $attrs['a11yHint']);
        }
    }

    /** primary | secondary | destructive | ghost. Default: primary. */
    public function variant(string $value): static
    {
        $this->buttonProps['variant'] = $value;

        return $this;
    }

    /** sm | md | lg. Default: md. */
    public function size(string $value): static
    {
        $this->buttonProps['size'] = $value;

        return $this;
    }

    public function disabled(bool $value = true): static
    {
        $this->buttonProps['disabled'] = $value;

        return $this;
    }

    public function loading(bool $value = true): static
    {
        $this->buttonProps['loading'] = $value;

        return $this;
    }

    public function icon(string $name): static
    {
        // Stored as `leading_icon` to match the interned prop key table
        // (NPUI_KEY_LEADING_ICON = 37). Using an interned key means the prop
        // transmits as a 1-byte index rather than the fallback length-prefixed
        // string path, which is slightly faster and less error-prone.
        $this->buttonProps['leading_icon'] = $name;

        return $this;
    }

    public function iconTrailing(string $name): static
    {
        // Stored as `trailing_icon` to match the interned prop key (38).
        $this->buttonProps['trailing_icon'] = $name;

        return $this;
    }

    public function a11yLabel(string $value): static
    {
        $this->buttonProps['a11y_label'] = $value;

        return $this;
    }

    public function a11yHint(string $value): static
    {
        $this->buttonProps['a11y_hint'] = $value;

        return $this;
    }

    public function onPress(string $method): static
    {
        $this->pressCallback = $method;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        $props = $this->buttonProps;

        if ($this->pressCallback !== null) {
            $props['on_press'] = $registry->register($this->pressCallback);
        }

        return $props;
    }

    // ── Model 3 enforcement ──────────────────────────────────────────────────
    //
    // Button controls its own visuals via `variant` + theme tokens. Per-instance
    // style overrides (bg, border, radius, shadow, opacity, elevation) and
    // internal padding are intentionally ignored. This prevents the collector's
    // applyStyle() from painting a wrapper around the native button.
    //
    // Legit layout props still pass through: margin, width/height/fill, flex,
    // alignSelf. They position the button within its parent.

    public function getStyle(): array
    {
        return [];
    }

    public function getLayout(): array
    {
        $layout = parent::getLayout();
        unset($layout['padding']);

        return $layout;
    }
}