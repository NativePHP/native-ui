<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;
use Native\Mobile\Icon\IconResolver;
use Native\Mobile\Icon\MaterialSymbol;
use Native\Mobile\Icon\SFSymbol;

class Icon extends Element
{
    protected string $type = 'icon';

    protected array $iconProps = [];

    private ?string $shared = null;
    private SFSymbol|string|null $sfOverride = null;
    private MaterialSymbol|string|null $materialOverride = null;

    public static function make(string $name = ''): static
    {
        $el = new static;

        if ($name !== '') {
            $el->name($name);
        }

        return $el;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['name']))  { $this->name($attrs['name']); }
        if (isset($attrs['size']))  { $this->size((float) $attrs['size']); }
        if (isset($attrs['color'])) { $this->color($attrs['color']); }

        // Optional dark-mode override hex. Renderers pick this when the
        // system colorScheme is dark; otherwise they use `color`.
        if (isset($attrs['dark-color']) || isset($attrs['darkColor'])) {
            $this->darkColor($attrs['dark-color'] ?? $attrs['darkColor']);
        }
    }

    /**
     * Set the icon. Mirrors the `(name, sf, material)` shape used by
     * `HasPlatformIcon`-bearing builders — the Icon element doesn't mix
     * in the trait directly because its public setter is `name()`
     * (matching the `<native:icon name="…">` blade attr) rather than
     * `icon()`.
     *
     *   <native:icon name="home" />
     *   Icon::make()->name(sf: SF::House, material: Material::Home)
     */
    public function name(
        ?string $name = null,
        SFSymbol|string|null $sf = null,
        MaterialSymbol|string|null $material = null,
    ): static {
        if ($name !== null)     { $this->shared = $name; }
        if ($sf !== null)       { $this->sfOverride = $sf; }
        if ($material !== null) { $this->materialOverride = $material; }

        return $this;
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
        $props = $this->iconProps;

        $resolved = IconResolver::resolve($this->shared, $this->sfOverride, $this->materialOverride);
        if ($resolved['icon'] !== null) {
            $props['name'] = $resolved['icon'];
            if ($resolved['variant'] !== null) {
                $props['material_variant'] = $resolved['variant'];
            }
        }

        return $props;
    }
}