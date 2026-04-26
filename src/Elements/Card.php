<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

/**
 * Card — content surface with three variants.
 *
 *   filled    (default) — surface + onSurface (emphasis level medium)
 *   outlined            — surface + outline stroke (emphasis level lower)
 *   elevated            — surface + shadow (emphasis level higher)
 *
 * Model 3: all colors come from theme tokens; radius uses `radius-md`. For
 * custom visuals, drop to `<native:column class="bg-theme-surface rounded-xl">`
 * or a `<native:pressable>` wrapper.
 */
class Card extends Element
{
    protected string $type = 'card';

    /** @var array<string, mixed> */
    protected array $cardProps = [];

    public static function make(): static
    {
        return new static;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['variant'])) {
            // Accept both the legacy int form (0/1/2) and the new string form.
            $raw = $attrs['variant'];
            if (is_numeric($raw)) {
                $this->variant(match ((int) $raw) {
                    1 => 'outlined',
                    2 => 'elevated',
                    default => 'filled',
                });
            } else {
                $this->variant((string) $raw);
            }
        }
        if (! empty($attrs['filled']))   { $this->filled(); }
        if (! empty($attrs['outlined'])) { $this->outlined(); }
        if (! empty($attrs['elevated'])) { $this->elevated(); }

        if (isset($attrs['a11y-label']) || isset($attrs['a11yLabel'])) {
            $this->a11yLabel($attrs['a11y-label'] ?? $attrs['a11yLabel']);
        }
        if (isset($attrs['a11y-hint']) || isset($attrs['a11yHint'])) {
            $this->a11yHint($attrs['a11y-hint'] ?? $attrs['a11yHint']);
        }
    }

    public function variant(string $variant): static
    {
        $this->cardProps['variant'] = $variant;

        return $this;
    }

    public function filled(): static { return $this->variant('filled'); }
    public function outlined(): static { return $this->variant('outlined'); }
    public function elevated(): static { return $this->variant('elevated'); }

    public function a11yLabel(string $value): static
    {
        $this->cardProps['a11y_label'] = $value;

        return $this;
    }

    public function a11yHint(string $value): static
    {
        $this->cardProps['a11y_hint'] = $value;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        return $this->cardProps;
    }
}
