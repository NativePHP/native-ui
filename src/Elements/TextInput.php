<?php

namespace Nativephp\ComposeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

class TextInput extends Element
{
    protected string $type = 'text_input';

    protected array $inputProps = [];

    protected ?string $changeCallback = null;

    protected ?string $submitCallback = null;

    public static function make(): static
    {
        return new static;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['value'])) {
            $this->value($attrs['value']);
        }
        if (isset($attrs['placeholder'])) {
            $this->placeholder($attrs['placeholder']);
        }
        if (isset($attrs['label'])) {
            $this->label($attrs['label']);
        }
        if (isset($attrs['keyboard'])) {
            $this->keyboard((int) $attrs['keyboard']);
        }
        if (! empty($attrs['secure'])) {
            $this->secure();
        }
        if (isset($attrs['maxLength'])) {
            $this->maxLength((int) $attrs['maxLength']);
        }
        if (! empty($attrs['multiline'])) {
            $this->multiline();
        }
        if (isset($attrs['variant'])) {
            $this->variant((int) $attrs['variant']);
        }
        if (! empty($attrs['disabled'])) {
            $this->disabled();
        }
        if (! empty($attrs['readOnly'])) {
            $this->readOnly();
        }
        if (! empty($attrs['isError'])) {
            $this->error();
        }
        if (isset($attrs['prefix'])) {
            $this->prefix($attrs['prefix']);
        }
        if (isset($attrs['suffix'])) {
            $this->suffix($attrs['suffix']);
        }
        if (isset($attrs['supporting'])) {
            $this->supporting($attrs['supporting']);
        }
        if (isset($attrs['leadingIcon'])) {
            $this->leadingIcon($attrs['leadingIcon']);
        }
        if (isset($attrs['trailingIcon'])) {
            $this->trailingIcon($attrs['trailingIcon']);
        }
        if (isset($attrs['maxLines'])) {
            $this->maxLines((int) $attrs['maxLines']);
        }
        if (isset($attrs['minLines'])) {
            $this->minLines((int) $attrs['minLines']);
        }
        if (isset($attrs['fontSize'])) {
            $this->fontSize((float) $attrs['fontSize']);
        }
        if (isset($attrs['fontWeight'])) {
            $this->fontWeight((int) $attrs['fontWeight']);
        }
        if (isset($attrs['textColor'])) {
            $this->textColor($attrs['textColor']);
        }
        if (isset($attrs['color'])) {
            $this->color($attrs['color']);
        }
        if (isset($attrs['containerColor'])) {
            $this->containerColor($attrs['containerColor']);
        }
        if (isset($attrs['labelColor'])) {
            $this->labelColor($attrs['labelColor']);
        }
        if (isset($attrs['supportingColor'])) {
            $this->supportingColor($attrs['supportingColor']);
        }
    }

    // Value & text

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

    // Variant

    public function variant(int $variant): static
    {
        $this->inputProps['variant'] = $variant;

        return $this;
    }

    public function outlined(): static
    {
        $this->inputProps['variant'] = 0;

        return $this;
    }

    public function filled(): static
    {
        $this->inputProps['variant'] = 1;

        return $this;
    }

    // State

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

    // Input behavior

    public function keyboard(int $type): static
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

    // Decorations

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

    public function supporting(string $text): static
    {
        $this->inputProps['supporting'] = $text;

        return $this;
    }

    public function leadingIcon(string $icon): static
    {
        $this->inputProps['leading_icon'] = $icon;

        return $this;
    }

    public function trailingIcon(string $icon): static
    {
        $this->inputProps['trailing_icon'] = $icon;

        return $this;
    }

    // Typography

    public function fontSize(float $size): static
    {
        $this->inputProps['font_size'] = $size;

        return $this;
    }

    public function fontWeight(int $weight): static
    {
        $this->inputProps['font_weight'] = $weight;

        return $this;
    }

    public function textColor(string $color): static
    {
        $this->inputProps['text_color'] = $color;

        return $this;
    }

    public function color(string $color): static
    {
        $this->inputProps['color'] = $color;

        return $this;
    }

    public function containerColor(string $color): static
    {
        $this->inputProps['container_color'] = $color;

        return $this;
    }

    public function labelColor(string $color): static
    {
        $this->inputProps['label_color'] = $color;

        return $this;
    }

    public function supportingColor(string $color): static
    {
        $this->inputProps['supporting_color'] = $color;

        return $this;
    }

    // Callbacks

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
}
