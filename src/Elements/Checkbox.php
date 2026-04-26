<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

/**
 * Checkbox — binary tick/untick with optional inline label.
 *
 * On iOS renders as a SF Symbol checkmark (no native checkbox primitive in
 * SwiftUI — Apple's HIG prefers toggles); on Android uses M3 `Checkbox`.
 *
 * Model 3: no per-instance color overrides. Check/border/label colors come
 * from theme tokens.
 */
class Checkbox extends Element
{
    protected string $type = 'checkbox';

    /** @var array<string, mixed> */
    protected array $checkboxProps = [];

    protected ?string $changeCallback = null;

    public static function make(): static
    {
        return new static;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['value']))    { $this->value((bool) $attrs['value']); }
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
        if (isset($attrs['debounce-ms']) || isset($attrs['debounceMs'])) {
            $this->debounceMs((int) ($attrs['debounce-ms'] ?? $attrs['debounceMs']));
        }
    }

    public function value(bool $checked): static
    {
        $this->checkboxProps['value'] = $checked;

        return $this;
    }

    public function label(string $label): static
    {
        $this->checkboxProps['label'] = $label;

        return $this;
    }

    public function disabled(bool $value = true): static
    {
        $this->checkboxProps['disabled'] = $value;

        return $this;
    }

    public function a11yLabel(string $value): static
    {
        $this->checkboxProps['a11y_label'] = $value;

        return $this;
    }

    public function a11yHint(string $value): static
    {
        $this->checkboxProps['a11y_hint'] = $value;

        return $this;
    }

    public function syncMode(string $mode): static
    {
        $this->checkboxProps['sync_mode'] = $mode;

        return $this;
    }

    public function debounceMs(int $ms): static
    {
        $this->checkboxProps['debounce_ms'] = $ms;

        return $this;
    }

    public function onChange(string $method): static
    {
        $this->changeCallback = $method;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        $props = $this->checkboxProps;

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
