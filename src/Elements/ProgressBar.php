<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

/**
 * Linear progress bar. Value in [0.0, 1.0]. Omit `value` for indeterminate
 * (animated wave).
 *
 * Model 3: `theme.primary` fills the progress; `theme.surfaceVariant`
 * backs the track. No per-instance color overrides.
 */
class ProgressBar extends Element
{
    protected string $type = 'progress_bar';

    /** @var array<string, mixed> */
    protected array $progressBarProps = [];

    public static function make(): static
    {
        return new static;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['value'])) {
            $this->value((float) $attrs['value']);
        }
        if (! empty($attrs['indeterminate'])) {
            $this->indeterminate();
        }
        if (isset($attrs['color']))      { $this->color((string) $attrs['color']); }
        if (isset($attrs['trackColor'])) { $this->trackColor((string) $attrs['trackColor']); }

        if (isset($attrs['a11y-label']) || isset($attrs['a11yLabel'])) {
            $this->a11yLabel($attrs['a11y-label'] ?? $attrs['a11yLabel']);
        }
    }

    /**
     * Optional fill color override. Leave unset for the theme-driven default
     * (`theme.primary`). Useful on primitives embedded in non-theme-styled
     * containers (e.g. a red progress bar on a dashboard card with custom bg).
     */
    public function color(string $hex): static
    {
        $this->progressBarProps['color'] = $hex;

        return $this;
    }

    public function trackColor(string $hex): static
    {
        $this->progressBarProps['track_color'] = $hex;

        return $this;
    }

    public function value(float $val): static
    {
        $this->progressBarProps['value'] = $val;
        $this->progressBarProps['indeterminate'] = false;

        return $this;
    }

    public function indeterminate(bool $value = true): static
    {
        $this->progressBarProps['indeterminate'] = $value;

        return $this;
    }

    public function a11yLabel(string $value): static
    {
        $this->progressBarProps['a11y_label'] = $value;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        return $this->progressBarProps;
    }
}
