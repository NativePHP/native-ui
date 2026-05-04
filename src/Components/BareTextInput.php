<?php

namespace Nativephp\NativeUi\Components;

use Native\Mobile\Edge\Components\Native\NativeBladeComponent;

class BareTextInput extends NativeBladeComponent
{
    protected function elementType(): string
    {
        return 'bare_text_input';
    }
}
