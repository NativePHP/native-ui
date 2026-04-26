<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

/**
 * Shared base for the text input variants (`outlined-text-input`,
 * `filled-text-input`).
 *
 * API shape follows Model 3 — NO per-instance color / font overrides. All
 * colors, corner radii, and typography come from the theme. For fully custom
 * input styling, drop to `<native:pressable>` wrapping a plain HTML-like form.
 *
 * Allowed per-instance:
 *   - `value`, `placeholder`, `label`, `supporting`  (content)
 *   - `disabled`, `readOnly`, `error`, `loading`     (state)
 *   - `keyboard`, `secure`, `maxLength`, `multiline`, `maxLines`, `minLines` (behavior)
 *   - `prefix`, `suffix`, `leading-icon`, `trailing-icon` (decorations)
 *   - `size`                                          (sm | md | lg)
 *   - `a11y-label`, `a11y-hint`                       (accessibility)
 *   - `@change`, `@submit`                            (callbacks)
 *
 * Subclasses (`OutlinedTextInput`, `FilledTextInput`) only override `$type`
 * so native renderers can dispatch to the right Material3 / SwiftUI primitive.
 */
abstract class BaseTextInput extends Element
{
    /** @var array<string, mixed> */
    protected array $inputProps = [];

    protected ?string $changeCallback = null;

    protected ?string $submitCallback = null;

    public static function make(): static
    {
        return new static;
    }

    public function applyAttributes(array $attrs): void
    {
        // Content
        if (isset($attrs['value']))       { $this->value($attrs['value']); }
        if (isset($attrs['placeholder'])) { $this->placeholder($attrs['placeholder']); }
        if (isset($attrs['label']))       { $this->label($attrs['label']); }
        if (isset($attrs['supporting']))  { $this->supporting($attrs['supporting']); }

        // State
        if (! empty($attrs['disabled']))  { $this->disabled(); }
        if (! empty($attrs['readOnly']) || ! empty($attrs['read-only'])) {
            $this->readOnly();
        }
        if (! empty($attrs['error']) || ! empty($attrs['isError']) || ! empty($attrs['is-error'])) {
            $this->error();
        }
        if (! empty($attrs['loading']))   { $this->loading(); }

        // Behavior
        if (isset($attrs['keyboard']))    { $this->keyboard($attrs['keyboard']); }
        if (! empty($attrs['secure']))    { $this->secure(); }
        if (isset($attrs['maxLength']) || isset($attrs['max-length'])) {
            $this->maxLength((int) ($attrs['maxLength'] ?? $attrs['max-length']));
        }
        if (! empty($attrs['multiline'])) { $this->multiline(); }
        if (isset($attrs['maxLines']) || isset($attrs['max-lines'])) {
            $this->maxLines((int) ($attrs['maxLines'] ?? $attrs['max-lines']));
        }
        if (isset($attrs['minLines']) || isset($attrs['min-lines'])) {
            $this->minLines((int) ($attrs['minLines'] ?? $attrs['min-lines']));
        }

        // Decorations
        if (isset($attrs['prefix']))      { $this->prefix($attrs['prefix']); }
        if (isset($attrs['suffix']))      { $this->suffix($attrs['suffix']); }
        if (isset($attrs['leading-icon']) || isset($attrs['leadingIcon'])) {
            $this->leadingIcon($attrs['leading-icon'] ?? $attrs['leadingIcon']);
        }
        if (isset($attrs['trailing-icon']) || isset($attrs['trailingIcon'])) {
            $this->trailingIcon($attrs['trailing-icon'] ?? $attrs['trailingIcon']);
        }

        // Size + a11y
        if (isset($attrs['size']))        { $this->size($attrs['size']); }
        if (isset($attrs['a11y-label']) || isset($attrs['a11yLabel'])) {
            $this->a11yLabel($attrs['a11y-label'] ?? $attrs['a11yLabel']);
        }
        if (isset($attrs['a11y-hint']) || isset($attrs['a11yHint'])) {
            $this->a11yHint($attrs['a11y-hint'] ?? $attrs['a11yHint']);
        }

        // Sync mode + debounce (from `native:model` expansion, or set manually).
        if (isset($attrs['sync-mode']) || isset($attrs['syncMode'])) {
            $this->syncMode($attrs['sync-mode'] ?? $attrs['syncMode']);
        }
        if (isset($attrs['debounce-ms']) || isset($attrs['debounceMs'])) {
            $this->debounceMs((int) ($attrs['debounce-ms'] ?? $attrs['debounceMs']));
        }
    }

    // ── Content ──────────────────────────────────────────────────────────────

    public function value(string $text): static
    {
        $this->inputProps['value'] = $text;

        return $this;
    }

    public function placeholder(string $text): static
    {
        $this->inputProps['placeholder'] = $text;

        return $this;
    }

    public function label(string $text): static
    {
        $this->inputProps['label'] = $text;

        return $this;
    }

    public function supporting(string $text): static
    {
        $this->inputProps['supporting'] = $text;

        return $this;
    }

    // ── State ────────────────────────────────────────────────────────────────

    public function disabled(bool $value = true): static
    {
        $this->inputProps['disabled'] = $value;

        return $this;
    }

    public function readOnly(bool $value = true): static
    {
        $this->inputProps['read_only'] = $value;

        return $this;
    }

    public function error(bool $value = true): static
    {
        $this->inputProps['is_error'] = $value;

        return $this;
    }

    public function loading(bool $value = true): static
    {
        $this->inputProps['loading'] = $value;

        return $this;
    }

    // ── Behavior ─────────────────────────────────────────────────────────────

    /** Keyboard hint — "text" (default) | "number" | "email" | "phone" | "url" | "decimal" | "password" */
    public function keyboard(string|int $type): static
    {
        $this->inputProps['keyboard'] = $type;

        return $this;
    }

    public function secure(bool $value = true): static
    {
        $this->inputProps['secure'] = $value;

        return $this;
    }

    public function maxLength(int $length): static
    {
        $this->inputProps['max_length'] = $length;

        return $this;
    }

    public function multiline(bool $value = true): static
    {
        $this->inputProps['multiline'] = $value;

        return $this;
    }

    public function maxLines(int $lines): static
    {
        $this->inputProps['max_lines'] = $lines;

        return $this;
    }

    public function minLines(int $lines): static
    {
        $this->inputProps['min_lines'] = $lines;

        return $this;
    }

    // ── Decorations ──────────────────────────────────────────────────────────

    public function prefix(string $text): static
    {
        $this->inputProps['prefix'] = $text;

        return $this;
    }

    public function suffix(string $text): static
    {
        $this->inputProps['suffix'] = $text;

        return $this;
    }

    public function leadingIcon(string $name): static
    {
        $this->inputProps['leading_icon'] = $name;

        return $this;
    }

    public function trailingIcon(string $name): static
    {
        $this->inputProps['trailing_icon'] = $name;

        return $this;
    }

    // ── Size + a11y ──────────────────────────────────────────────────────────

    /** sm | md | lg. Default: md. */
    public function size(string $value): static
    {
        $this->inputProps['size'] = $value;

        return $this;
    }

    public function a11yLabel(string $value): static
    {
        $this->inputProps['a11y_label'] = $value;

        return $this;
    }

    public function a11yHint(string $value): static
    {
        $this->inputProps['a11y_hint'] = $value;

        return $this;
    }

    // ── Sync mode ────────────────────────────────────────────────────────────

    /**
     * How the native side should dispatch change events back to PHP.
     *
     *   'live'     — every keystroke (default, matches `wire:model.live`)
     *   'blur'     — only when the field loses focus / user submits
     *   'debounce' — after `debounce_ms` of inactivity
     *
     * Typically set indirectly via `native:model.live` / `.blur` / `.debounce.Xms`
     * in Blade — the precompiler translates those into this prop.
     */
    public function syncMode(string $mode): static
    {
        $this->inputProps['sync_mode'] = $mode;

        return $this;
    }

    public function debounceMs(int $ms): static
    {
        $this->inputProps['debounce_ms'] = $ms;

        return $this;
    }

    // ── Callbacks ────────────────────────────────────────────────────────────

    public function onChange(string $method): static
    {
        $this->changeCallback = $method;

        return $this;
    }

    public function onSubmit(string $method): static
    {
        $this->submitCallback = $method;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        $props = $this->inputProps;

        if ($this->changeCallback !== null) {
            $props['on_change'] = $registry->register($this->changeCallback);
        }
        if ($this->submitCallback !== null) {
            $props['on_submit'] = $registry->register($this->submitCallback);
        }

        return $props;
    }

    // ── Model 3 enforcement ──────────────────────────────────────────────────
    //
    // Text inputs control their own visuals via variant + theme tokens.
    // Per-instance style overrides (bg, border, radius, shadow, opacity,
    // elevation) and internal padding are intentionally ignored. This
    // prevents the collector's applyStyle() from painting a wrapper around
    // the native input.

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
