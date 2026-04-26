<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

/**
 * Bottom sheet — dismissible panel that slides up from the bottom. Visibility
 * driven by `visible`; `@dismiss` fires on drag-down or tap-outside.
 *
 * Model 3: container colors from theme. No per-instance `backgroundColor`
 * override — wrap content in `<native:column class="bg-...">` if a
 * non-standard surface is truly needed, but prefer the theme.
 */
class BottomSheet extends Element
{
    protected string $type = 'bottom_sheet';

    /** @var array<string, mixed> */
    protected array $sheetProps = [];

    protected ?string $dismissCallback = null;

    public static function make(): static
    {
        return new static;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['visible'])) { $this->visible((bool) $attrs['visible']); }
        if (isset($attrs['detents'])) { $this->detents($attrs['detents']); }

        if (isset($attrs['a11y-label']) || isset($attrs['a11yLabel'])) {
            $this->a11yLabel($attrs['a11y-label'] ?? $attrs['a11yLabel']);
        }
    }

    public function visible(bool $value = true): static
    {
        $this->sheetProps['visible'] = $value;

        return $this;
    }

    /**
     * Set allowed sheet heights.
     * Accepts: "small", "medium", "large", "full", or comma-separated
     * ("medium,large"). Also accepts a numeric fraction (0.0–1.0) for a
     * custom height (e.g. "0.4" for 40% of screen).
     */
    public function detents(string $detents): static
    {
        $this->sheetProps['detents'] = $detents;

        return $this;
    }

    public function a11yLabel(string $value): static
    {
        $this->sheetProps['a11y_label'] = $value;

        return $this;
    }

    public function onDismiss(string $method): static
    {
        $this->dismissCallback = $method;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        $props = $this->sheetProps;

        if ($this->dismissCallback !== null) {
            $props['on_dismiss'] = $registry->register($this->dismissCallback);
        }

        return $props;
    }
}
