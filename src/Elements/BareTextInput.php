<?php

namespace Nativephp\NativeUi\Elements;

/**
 * Chromeless text input — a SwiftUI `TextField` (iOS) / Compose
 * `BasicTextField` (Android) with no outline, no label, no fill, no
 * Material 3 styling. Just the typing affordance.
 *
 * Intended for places where the surrounding container provides the
 * visual chrome — chat input pills, search bars, inline editors, etc.
 * Pair with a `<native:row class="glass rounded-full">` wrapper to get
 * the iMessage / WhatsApp pill aesthetic.
 *
 * Inherits all behaviour (value sync via `native:model`, echo
 * prevention, sync_mode, secure / multiline, keyboard type, submit,
 * disabled / readOnly) from `BaseTextInput`. Variant differences are
 * purely visual.
 */
class BareTextInput extends BaseTextInput
{
    protected string $type = 'bare_text_input';
}
