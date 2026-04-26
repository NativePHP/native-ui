<?php

namespace Nativephp\NativeUi\Components;

use Native\Mobile\Edge\Components\Native\NativeBladeComponent;

class OutlinedTextInput extends NativeBladeComponent
{
    protected function elementType(): string
    {
        return 'outlined_text_input';
    }
}