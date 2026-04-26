<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

/**
 * Circular activity indicator (spinner). Always indeterminate — use
 * `<native:progress-bar :value="..."/>` for determinate progress.
 *
 * Model 3: `theme.primary` tint. No per-instance color overrides.
 * Size: sm | md (default) | lg.
 */
class ActivityIndicator extends Element
{
    protected string $type = 'activity_indicator';

    /** @var array<string, mixed> */
    protected array $indicatorProps = [];

    public static function make(): static
    {
        return new static;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['size']))  { $this->size($attrs['size']); }
        if (isset($attrs['color'])) { $this->color((string) $attrs['color']); }

        if (isset($attrs['a11y-label']) || isset($attrs['a11yLabel'])) {
            $this->a11yLabel($attrs['a11y-label'] ?? $attrs['a11yLabel']);
        }
    }

    /**
     * Optional color override. Primitives like spinners sometimes need to
     * match their container (e.g. a light spinner on a dark image overlay)
     * and the Model 3 rule yields ergonomics here. Leave unset for the
     * theme-driven default (`theme.primary`).
     */
    public function color(string $hex): static
    {
        $this->indicatorProps['color'] = $hex;

        return $this;
    }

    /** Accepts "sm" | "md" | "lg" or legacy ints (1=large, 2=small). */
    public function size(string|int $size): static
    {
        $this->indicatorProps['size'] = match ($size) {
            'lg', 'large', 1 => 'lg',
            'sm', 'small', 2 => 'sm',
            default => 'md',
        };

        return $this;
    }

    public function a11yLabel(string $value): static
    {
        $this->indicatorProps['a11y_label'] = $value;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        return $this->indicatorProps;
    }
}
