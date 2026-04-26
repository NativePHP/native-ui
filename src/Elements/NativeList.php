<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

class NativeList extends Element
{
    protected string $type = 'list';

    protected array $listProps = [];

    protected ?string $refreshCallback = null;

    protected ?string $endReachedCallback = null;

    public static function make(Element ...$children): static
    {
        $el = new static;
        $el->children = $children;

        return $el;
    }

    public function applyAttributes(array $attrs): void
    {
        if (! empty($attrs['horizontal'])) {
            $this->horizontal();
        }
        if (isset($attrs['showsIndicators']) || isset($attrs['shows-indicators'])) {
            $this->showsIndicators((bool) ($attrs['showsIndicators'] ?? $attrs['shows-indicators']));
        }
        if (! empty($attrs['separator'])) {
            $this->separator();
        }
        if (isset($attrs['on-refresh']) || isset($attrs['onRefresh'])) {
            $this->onRefresh($attrs['on-refresh'] ?? $attrs['onRefresh']);
        }
        if (isset($attrs['on-end-reached']) || isset($attrs['onEndReached'])) {
            $this->onEndReached($attrs['on-end-reached'] ?? $attrs['onEndReached']);
        }
    }

    public function horizontal(bool $value = true): static
    {
        $this->listProps['horizontal'] = $value;

        return $this;
    }

    public function showsIndicators(bool $value = true): static
    {
        $this->listProps['shows_indicators'] = $value;

        return $this;
    }

    public function separator(bool $value = true): static
    {
        $this->listProps['separator'] = $value;

        return $this;
    }

    public function onRefresh(string $method): static
    {
        $this->refreshCallback = $method;

        return $this;
    }

    public function onEndReached(string $method): static
    {
        $this->endReachedCallback = $method;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        $props = $this->listProps;

        if ($this->refreshCallback !== null) {
            $props['on_refresh'] = $registry->register($this->refreshCallback);
        }

        if ($this->endReachedCallback !== null) {
            $props['on_end_reached'] = $registry->register($this->endReachedCallback);
        }

        return $props;
    }
}
