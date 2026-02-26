<?php

namespace Nativephp\ComposeUi\Components;

use Native\Mobile\Edge\Components\Native\NativeBladeComponent;

class Button extends NativeBladeComponent
{
    protected bool $isSelfClosing = true;

    protected function elementType(): string
    {
        return 'button';
    }
}