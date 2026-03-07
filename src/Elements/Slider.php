<?php

namespace Nativephp\ComposeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

class Slider extends Element
{
    protected string $type = 'slider';

    protected array $sliderProps = [];

    protected ?string $changeCallback = null;

    public static function make(): static
    {
        return new static;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['value'])) {
            $this->value((float) $attrs['value']);
        }
        if (isset($attrs['min'])) {
            $this->min((float) $attrs['min']);
        }
        if (isset($attrs['max'])) {
            $this->max((float) $attrs['max']);
        }
        if (isset($attrs['step'])) {
            $this->step((float) $attrs['step']);
        }
        if (isset($attrs['color'])) {
            $this->color($attrs['color']);
        }
        if (isset($attrs['trackColor'])) {
            $this->trackColor($attrs['trackColor']);
        }
        if (! empty($attrs['disabled'])) {
            $this->disabled();
        }
    }

    public function value(float $val): static
    {
        $this->sliderProps['value'] = $val;

        return $this;
    }

    public function min(float $val): static
    {
        $this->sliderProps['min'] = $val;

        return $this;
    }

    public function max(float $val): static
    {
        $this->sliderProps['max'] = $val;

        return $this;
    }

    public function step(float $val): static
    {
        $this->sliderProps['step'] = $val;

        return $this;
    }

    public function color(string $color): static
    {
        $this->sliderProps['color'] = $color;

        return $this;
    }

    public function trackColor(string $color): static
    {
        $this->sliderProps['track_color'] = $color;

        return $this;
    }

    public function disabled(bool $value = true): static
    {
        $this->sliderProps['disabled'] = $value;

        return $this;
    }

    public function onChange(string $method): static
    {
        $this->changeCallback = $method;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        $props = $this->sliderProps;

        if ($this->changeCallback !== null) {
            $props['on_change'] = $registry->register($this->changeCallback);
        }

        return $props;
    }
}
