<?php

namespace Nativephp\NativeUi\Components;

use Native\Mobile\Edge\Components\Native\NativeBladeComponent;

class Card extends NativeBladeComponent
{
    protected function elementType(): string
    {
        return 'card';
    }
}
