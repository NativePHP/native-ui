<?php

namespace Nativephp\NativeUi\Components;

use Native\Mobile\Edge\Components\Native\NativeBladeComponent;

class Screen extends NativeBladeComponent
{
    protected function elementType(): string
    {
        return 'screen';
    }
}