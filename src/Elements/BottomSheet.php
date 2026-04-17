<?php

namespace Nativephp\ComposeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

class BottomSheet extends Element
{
    protected string $type = 'bottom_sheet';

    protected array $sheetProps = [];

    protected ?string $dismissCallback = null;

    public static function make(): static
    {
        return new static;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['visible'])) {
            $this->visible((bool) $attrs['visible']);
        }
        if (isset($attrs['detents'])) {
            $this->detents($attrs['detents']);
        }
        if (isset($attrs['background-color']) || isset($attrs['backgroundColor'])) {
            $this->backgroundColor($attrs['background-color'] ?? $attrs['backgroundColor']);
        }
    }

    public function backgroundColor(string $color): static
    {
        $this->sheetProps['background_color'] = $color;

        return $this;
    }

    public function visible(bool $value = true): static
    {
        $this->sheetProps['visible'] = $value;

        return $this;
    }

    /**
     * Set allowed sheet heights.
     * Accepts: "small", "medium", "large", "full", or comma-separated like "medium,large"
     * Also accepts a float (0.0-1.0) for a custom fraction of screen height.
     */
    public function detents(string $detents): static
    {
        $this->sheetProps['detents'] = $detents;

        return $this;
    }

    public function onDismiss(string $method): static
    {
        $this->dismissCallback = $method;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        $props = $this->sheetProps;

        if ($this->dismissCallback !== null) {
            $props['on_dismiss'] = $registry->register($this->dismissCallback);
        }

        return $props;
    }
}
