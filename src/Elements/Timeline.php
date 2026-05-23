<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

class Timeline extends Element
{
    protected string $type = 'timeline';

    /** @var array<string, mixed> */
    protected array $timelineProps = [];

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['orientation'])) {
            $this->orientation((string) $attrs['orientation']);
        }

        if (isset($attrs['horizontal']) && filter_var($attrs['horizontal'], FILTER_VALIDATE_BOOL)) {
            $this->orientation('horizontal');
        }

        if (isset($attrs['vertical']) && filter_var($attrs['vertical'], FILTER_VALIDATE_BOOL)) {
            $this->orientation('vertical');
        }
    }

    public function orientation(string $value): static
    {
        $this->timelineProps['orientation'] = $value;

        return $this;
    }

    protected function resolveProps(CallbackRegistry $registry): array
    {
        return $this->timelineProps;
    }
}
