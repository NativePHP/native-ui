<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

/**
 * Select — single-choice dropdown over a string option list.
 *
 * iOS: renders as a SwiftUI `Menu` (popover picker on tap).
 * Android: renders as an M3 `ExposedDropdownMenuBox` with an outlined trigger.
 *
 * Model 3: colors/borders from theme tokens. For custom visuals, drop to
 * `<native:pressable>` wrapping your own list UI.
 */
class Select extends Element
{
    protected string $type = 'select';

    /** @var array<string, mixed> */
    protected array $selectProps = [];

    protected ?string $changeCallback = null;

    public static function make(): static
    {
        return new static;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['value']))       { $this->value($attrs['value']); }
        if (isset($attrs['label']))       { $this->label($attrs['label']); }
        if (isset($attrs['placeholder'])) { $this->placeholder($attrs['placeholder']); }
        if (isset($attrs['options']))     { $this->options((array) $attrs['options']); }
        if (! empty($attrs['disabled']))  { $this->disabled(); }

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

    public function value(string $val): static
    {
        $this->selectProps['value'] = $val;

        return $this;
    }

    public function label(string $text): static
    {
        $this->selectProps['label'] = $text;

        return $this;
    }

    public function placeholder(string $text): static
    {
        $this->selectProps['placeholder'] = $text;

        return $this;
    }

    /** @param array<int|string, string> $options */
    public function options(array $options): static
    {
        $this->selectProps['options'] = array_values(array_map('strval', $options));

        return $this;
    }

    public function disabled(bool $value = true): static
    {
        $this->selectProps['disabled'] = $value;

        return $this;
    }

    public function a11yLabel(string $value): static
    {
        $this->selectProps['a11y_label'] = $value;

        return $this;
    }

    public function a11yHint(string $value): static
    {
        $this->selectProps['a11y_hint'] = $value;

        return $this;
    }

    public function syncMode(string $mode): static
    {
        $this->selectProps['sync_mode'] = $mode;

        return $this;
    }

    public function onChange(string $method): static
    {
        $this->changeCallback = $method;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        $props = $this->selectProps;

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

    public function getLayout(): array
    {
        $layout = parent::getLayout();
        unset($layout['padding']);

        return $layout;
    }
}
