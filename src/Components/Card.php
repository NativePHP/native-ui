<?php

namespace Nativephp\ComposeUi\Components;

use Native\Mobile\Edge\Components\Native\NativeBladeComponent;

class Card extends NativeBladeComponent
{
    protected function elementType(): string
    {
        return 'card';
    }
}
