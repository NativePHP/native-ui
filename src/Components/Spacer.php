<?php

namespace Nativephp\ComposeUi\Components;

use Native\Mobile\Edge\Components\Native\NativeBladeComponent;

class Spacer extends NativeBladeComponent
{
    protected bool $isSelfClosing = true;

    protected function elementType(): string
    {
        return 'spacer';
    }
}