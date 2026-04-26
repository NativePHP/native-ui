<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

/**
 * Badge — small count or text marker, typically used as an overlay on nav
 * items or buttons.
 *
 * Variants (semantic, color comes from theme):
 *   destructive (default) — theme.destructive / on-destructive
 *   primary               — theme.primary / on-primary
 *   accent                — theme.accent / on-accent
 *
 * Use either `count` (renders an integer; "99+" for >99) or `label` (arbitrary
 * short text). If both set, `label` wins.
 */
class Badge extends Element
{
    protected string $type = 'badge';

    /** @var array<string, mixed> */
    protected array $badgeProps = [];

    public static function make(): static
    {
        return new static;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['count']))   { $this->count((int) $attrs['count']); }
        if (isset($attrs['label']))   { $this->label($attrs['label']); }
        if (isset($attrs['variant'])) { $this->variant((string) $attrs['variant']); }

        if (isset($attrs['a11y-label']) || isset($attrs['a11yLabel'])) {
            $this->a11yLabel($attrs['a11y-label'] ?? $attrs['a11yLabel']);
        }
    }

    public function count(int $count): static
    {
        $this->badgeProps['count'] = $count;

        return $this;
    }

    public function label(string $text): static
    {
        $this->badgeProps['label'] = $text;

        return $this;
    }

    public function variant(string $variant): static
    {
        $this->badgeProps['variant'] = $variant;

        return $this;
    }

    public function a11yLabel(string $value): static
    {
        $this->badgeProps['a11y_label'] = $value;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        return $this->badgeProps;
    }
}
