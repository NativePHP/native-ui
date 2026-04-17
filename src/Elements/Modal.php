<?php

namespace Nativephp\ComposeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

class Modal extends Element
{
    protected string $type = 'modal';

    protected array $modalProps = [];

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
        if (isset($attrs['dismissible']) || isset($attrs['dismissable'])) {
            $this->dismissible((bool) ($attrs['dismissible'] ?? $attrs['dismissable']));
        }
    }

    public function visible(bool $value = true): static
    {
        $this->modalProps['visible'] = $value;

        return $this;
    }

    public function dismissible(bool $value = true): static
    {
        $this->modalProps['dismissible'] = $value;

        return $this;
    }

    public function onDismiss(string $method): static
    {
        $this->dismissCallback = $method;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        $props = $this->modalProps;

        if ($this->dismissCallback !== null) {
            $props['on_dismiss'] = $registry->register($this->dismissCallback);
        }

        return $props;
    }
}
