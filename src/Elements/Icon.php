<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

class Icon extends Element
{
    protected string $type = 'icon';

    protected array $iconProps = [];

    public static function make(string $name = ''): static
    {
        $el = new static;

        if ($name !== '') {
            $el->iconProps['name'] = $name;
        }

        return $el;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['name']))  { $this->iconProps['name'] = $attrs['name']; }
        if (isset($attrs['size']))  { $this->size((float) $attrs['size']); }
        if (isset($attrs['color'])) { $this->color($attrs['color']); }

        // Optional dark-mode override hex. Renderers pick this when the
        // system colorScheme is dark; otherwise they use `color`.
        if (isset($attrs['dark-color']) || isset($attrs['darkColor'])) {
            $this->darkColor($attrs['dark-color'] ?? $attrs['darkColor']);
        }
    }

    public function size(float $size): static
    {
        $this->iconProps['size'] = $size;

        return $this;
    }

    public function color(string $color): static
    {
        $this->iconProps['color'] = $color;

        return $this;
    }

    public function darkColor(string $color): static
    {
        $this->iconProps['dark_color'] = $color;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        return $this->iconProps;
    }
}