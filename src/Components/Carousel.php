<?php

namespace Nativephp\NativeUi\Components;

use Native\Mobile\Edge\Components\Native\NativeBladeComponent;

class Carousel extends NativeBladeComponent
{
    protected function elementType(): string
    {
        return 'carousel';
    }
}
