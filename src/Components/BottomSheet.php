<?php

namespace Nativephp\ComposeUi\Components;

use Native\Mobile\Edge\Components\Native\NativeBladeComponent;

class BottomSheet extends NativeBladeComponent
{
    protected function elementType(): string
    {
        return 'bottom_sheet';
    }
}
