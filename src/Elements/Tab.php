<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

/**
 * Tab — child of `<native:tab-row>`. Declares a label + optional leading icon.
 * Selection state is owned by the parent row (see [TabRow]).
 */
class Tab extends Element
{
    protected string $type = 'tab';

    /** @var array<string, mixed> */
    protected array $tabProps = [];

    public static function make(string $label = ''): static
    {
        $el = new static;
        if ($label !== '') {
            $el->tabProps['label'] = $label;
        }

        return $el;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['label'])) { $this->tabProps['label'] = $attrs['label']; }
        if (isset($attrs['icon']))  { $this->icon($attrs['icon']); }

        if (isset($attrs['a11y-label']) || isset($attrs['a11yLabel'])) {
            $this->tabProps['a11y_label'] = $attrs['a11y-label'] ?? $attrs['a11yLabel'];
        }
    }

    public function icon(string $icon): static
    {
        $this->tabProps['icon'] = $icon;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        return $this->tabProps;
    }

    // ── Model 3 enforcement ──────────────────────────────────────────────────

    public function getStyle(): array
    {
        return [];
    }
}
