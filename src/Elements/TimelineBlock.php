<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

class TimelineBlock extends Element
{
    protected string $type = 'timeline_block';

    /** @var array<string, mixed> */
    protected array $blockProps = [];

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['heading'])) {
            $this->heading((string) $attrs['heading']);
        }

        if (isset($attrs['icon'])) {
            $this->icon((string) $attrs['icon']);
        }

        if (isset($attrs['status'])) {
            $this->status((string) $attrs['status']);
        }
    }

    public function heading(string $value): static
    {
        $this->blockProps['heading'] = $value;

        return $this;
    }

    public function icon(string $value): static
    {
        $this->blockProps['icon'] = $value;

        return $this;
    }

    public function status(string $value): static
    {
        $this->blockProps['status'] = $value;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        return $this->blockProps;
    }
}
