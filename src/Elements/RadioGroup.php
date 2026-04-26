<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

/**
 * RadioGroup — single-choice container holding `<native:radio>` children.
 *
 * The group owns the selection; children declare their own `radioValue` +
 * label. Changing the selection fires one onChange with the new value.
 *
 * Model 3: colors from theme. Use `<native:pressable>` for custom visuals.
 */
class RadioGroup extends Element
{
    protected string $type = 'radio_group';

    /** @var array<string, mixed> */
    protected array $radioGroupProps = [];

    protected ?string $changeCallback = null;

    public static function make(Element ...$children): static
    {
        $el = new static;
        $el->children = $children;

        return $el;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['value']))    { $this->value($attrs['value']); }
        if (isset($attrs['label']))    { $this->label($attrs['label']); }
        if (! empty($attrs['disabled'])) { $this->disabled(); }

        if (isset($attrs['a11y-label']) || isset($attrs['a11yLabel'])) {
            $this->a11yLabel($attrs['a11y-label'] ?? $attrs['a11yLabel']);
        }
        if (isset($attrs['a11y-hint']) || isset($attrs['a11yHint'])) {
            $this->a11yHint($attrs['a11y-hint'] ?? $attrs['a11yHint']);
        }

        if (isset($attrs['sync-mode']) || isset($attrs['syncMode'])) {
            $this->syncMode($attrs['sync-mode'] ?? $attrs['syncMode']);
        }
    }

    public function value(string $selectedValue): static
    {
        $this->radioGroupProps['value'] = $selectedValue;

        return $this;
    }

    public function label(string $text): static
    {
        $this->radioGroupProps['label'] = $text;

        return $this;
    }

    public function disabled(bool $value = true): static
    {
        $this->radioGroupProps['disabled'] = $value;

        return $this;
    }

    public function a11yLabel(string $value): static
    {
        $this->radioGroupProps['a11y_label'] = $value;

        return $this;
    }

    public function a11yHint(string $value): static
    {
        $this->radioGroupProps['a11y_hint'] = $value;

        return $this;
    }

    public function syncMode(string $mode): static
    {
        $this->radioGroupProps['sync_mode'] = $mode;

        return $this;
    }

    public function onChange(string $method): static
    {
        $this->changeCallback = $method;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        $props = $this->radioGroupProps;

        if ($this->changeCallback !== null) {
            $props['on_change'] = $registry->register($this->changeCallback);
        }

        return $props;
    }

    // ── Model 3 enforcement ──────────────────────────────────────────────────

    public function getStyle(): array
    {
        return [];
    }
}
