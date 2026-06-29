<?php

namespace Nativephp\NativeUi\Elements\Concerns;

trait ConfiguresTextInputBehavior
{
    /** Keyboard hint: "text" (default) | "number" | "email" | "phone" | "url" | "decimal" | "password" */
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

    public function autoGrow(int $minLines = 1, int $maxLines = 5): static
    {
        $minLines = max(1, $minLines);
        $maxLines = max($minLines, $maxLines);

        $this->inputProps['multiline'] = true;
        $this->inputProps['auto_grow'] = true;
        $this->inputProps['min_lines'] = $minLines;
        $this->inputProps['max_lines'] = $maxLines;

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

    /**
     * How the native side should dispatch change events back to PHP.
     *
     *   'live'     - every keystroke (default, matches `wire:model.live`)
     *   'blur'     - only when the field loses focus / user submits
     *   'debounce' - after `debounce_ms` of inactivity
     *
     * Typically set indirectly via `native:model.live` / `.blur` / `.debounce.Xms`
     * in Blade; the precompiler translates those into this prop.
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
}
