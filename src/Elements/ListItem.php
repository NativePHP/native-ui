<?php

namespace Nativephp\NativeUi\Elements;

use Native\Mobile\Edge\CallbackRegistry;
use Native\Mobile\Edge\Element;

class ListItem extends Element
{
    protected string $type = 'list_item';

    protected array $listItemProps = [];

    protected ?string $leadingChangeCallback = null;

    protected ?string $trailingChangeCallback = null;

    protected ?string $trailingPressCallback = null;

    protected ?string $swipeDeleteCallback = null;

    public static function make(string $headline = ''): static
    {
        $el = new static;
        if ($headline !== '') {
            $el->listItemProps['headline'] = $headline;
        }

        return $el;
    }

    public function applyAttributes(array $attrs): void
    {
        if (isset($attrs['headline'])) {
            $this->listItemProps['headline'] = $attrs['headline'];
        }
        if (isset($attrs['supporting'])) {
            $this->supporting($attrs['supporting']);
        }
        if (isset($attrs['overline'])) {
            $this->overline($attrs['overline']);
        }

        // Leading content — type-based attributes
        if (isset($attrs['leadingIcon'])) {
            $this->leadingIcon($attrs['leadingIcon']);
        }
        if (isset($attrs['leadingAvatar'])) {
            $this->leadingAvatar($attrs['leadingAvatar']);
        }
        if (isset($attrs['leadingMonogram'])) {
            $this->leadingMonogram($attrs['leadingMonogram'], $attrs['leadingMonogramColor'] ?? null);
        }
        if (isset($attrs['leadingImage'])) {
            $this->leadingImage($attrs['leadingImage']);
        }
        if (isset($attrs['leadingCheckbox'])) {
            $this->leadingCheckbox((bool) $attrs['leadingCheckbox']);
        }
        if (isset($attrs['leadingRadio'])) {
            $this->leadingRadio((bool) $attrs['leadingRadio']);
        }

        // Trailing content — type-based attributes
        if (isset($attrs['trailingIcon'])) {
            $this->trailingIcon($attrs['trailingIcon']);
        }
        if (isset($attrs['trailingText'])) {
            $this->trailingText($attrs['trailingText']);
        }
        if (isset($attrs['trailingCheckbox'])) {
            $this->trailingCheckbox((bool) $attrs['trailingCheckbox']);
        }
        if (isset($attrs['trailingSwitch'])) {
            $this->trailingSwitch((bool) $attrs['trailingSwitch']);
        }
        if (isset($attrs['trailingIconButton'])) {
            $this->trailingIconButton($attrs['trailingIconButton']);
        }

        // Color attributes
        if (isset($attrs['headlineColor'])) {
            $this->headlineColor($attrs['headlineColor']);
        }
        if (isset($attrs['supportingColor'])) {
            $this->supportingColor($attrs['supportingColor']);
        }
        if (isset($attrs['overlineColor'])) {
            $this->overlineColor($attrs['overlineColor']);
        }
        if (isset($attrs['containerColor'])) {
            $this->containerColor($attrs['containerColor']);
        }
        if (isset($attrs['leadingIconColor'])) {
            $this->leadingIconColor($attrs['leadingIconColor']);
        }
        if (isset($attrs['trailingIconColor'])) {
            $this->trailingIconColor($attrs['trailingIconColor']);
        }
        if (isset($attrs['trailingTextColor'])) {
            $this->trailingTextColor($attrs['trailingTextColor']);
        }

        // Elevation
        if (isset($attrs['tonalElevation'])) {
            $this->tonalElevation((float) $attrs['tonalElevation']);
        }
        if (isset($attrs['shadowElevation'])) {
            $this->shadowElevation((float) $attrs['shadowElevation']);
        }

        // Disabled
        if (isset($attrs['disabled'])) {
            $this->disabled((bool) $attrs['disabled']);
        }

        // Swipe actions
        if (isset($attrs['on-swipe-delete']) || isset($attrs['onSwipeDelete'])) {
            $this->onSwipeDelete($attrs['on-swipe-delete'] ?? $attrs['onSwipeDelete']);
        }
    }

    public function onSwipeDelete(string $method): static
    {
        $this->swipeDeleteCallback = $method;

        return $this;
    }

    // ── Text content ─────────────────────────────────

    public function supporting(string $text): static
    {
        $this->listItemProps['supporting'] = $text;

        return $this;
    }

    public function overline(string $text): static
    {
        $this->listItemProps['overline'] = $text;

        return $this;
    }

    // ── Leading content ──────────────────────────────

    public function leadingIcon(string $icon): static
    {
        $this->listItemProps['leading_type'] = 'icon';
        $this->listItemProps['leading_value'] = $icon;
        $this->listItemProps['leading_icon'] = $icon;

        return $this;
    }

    public function leadingAvatar(string $url): static
    {
        $this->listItemProps['leading_type'] = 'avatar';
        $this->listItemProps['leading_value'] = $url;

        return $this;
    }

    public function leadingMonogram(string $initials, ?string $color = null): static
    {
        $this->listItemProps['leading_type'] = 'monogram';
        $this->listItemProps['leading_value'] = substr($initials, 0, 2);
        if ($color !== null) {
            $this->listItemProps['leading_monogram_color'] = $color;
        }

        return $this;
    }

    public function leadingImage(string $url): static
    {
        $this->listItemProps['leading_type'] = 'image';
        $this->listItemProps['leading_value'] = $url;

        return $this;
    }

    public function leadingCheckbox(bool $checked = false): static
    {
        $this->listItemProps['leading_type'] = 'checkbox';
        $this->listItemProps['leading_checked'] = $checked;

        return $this;
    }

    public function leadingRadio(bool $selected = false): static
    {
        $this->listItemProps['leading_type'] = 'radio';
        $this->listItemProps['leading_checked'] = $selected;

        return $this;
    }

    // ── Trailing content ─────────────────────────────

    public function trailingIcon(string $icon): static
    {
        $this->listItemProps['trailing_type'] = 'icon';
        $this->listItemProps['trailing_value'] = $icon;
        $this->listItemProps['trailing_icon'] = $icon;

        return $this;
    }

    public function trailingText(string $text): static
    {
        $this->listItemProps['trailing_type'] = 'text';
        $this->listItemProps['trailing_value'] = $text;

        return $this;
    }

    public function trailingCheckbox(bool $checked = false): static
    {
        $this->listItemProps['trailing_type'] = 'checkbox';
        $this->listItemProps['trailing_checked'] = $checked;

        return $this;
    }

    public function trailingSwitch(bool $checked = false): static
    {
        $this->listItemProps['trailing_type'] = 'switch';
        $this->listItemProps['trailing_checked'] = $checked;

        return $this;
    }

    public function trailingIconButton(string $icon): static
    {
        $this->listItemProps['trailing_type'] = 'icon_button';
        $this->listItemProps['trailing_value'] = $icon;

        return $this;
    }

    // ── Callbacks ────────────────────────────────────

    public function onLeadingChange(string $method): static
    {
        $this->leadingChangeCallback = $method;

        return $this;
    }

    public function onTrailingChange(string $method): static
    {
        $this->trailingChangeCallback = $method;

        return $this;
    }

    public function onTrailingPress(string $method): static
    {
        $this->trailingPressCallback = $method;

        return $this;
    }

    // ── Styling ──────────────────────────────────────

    public function headlineColor(string $color): static
    {
        $this->listItemProps['headline_color'] = $color;

        return $this;
    }

    public function supportingColor(string $color): static
    {
        $this->listItemProps['supporting_color'] = $color;

        return $this;
    }

    public function overlineColor(string $color): static
    {
        $this->listItemProps['overline_color'] = $color;

        return $this;
    }

    public function containerColor(string $color): static
    {
        $this->listItemProps['container_color'] = $color;

        return $this;
    }

    public function leadingIconColor(string $color): static
    {
        $this->listItemProps['leading_icon_color'] = $color;

        return $this;
    }

    public function trailingIconColor(string $color): static
    {
        $this->listItemProps['trailing_icon_color'] = $color;

        return $this;
    }

    public function trailingTextColor(string $color): static
    {
        $this->listItemProps['trailing_text_color'] = $color;

        return $this;
    }

    public function tonalElevation(float $dp): static
    {
        $this->listItemProps['tonal_elevation'] = $dp;

        return $this;
    }

    public function shadowElevation(float $dp): static
    {
        $this->listItemProps['shadow_elevation'] = $dp;

        return $this;
    }

    public function disabled(bool $disabled = true): static
    {
        $this->listItemProps['disabled'] = $disabled;

        return $this;
    }

    // ── Resolution ───────────────────────────────────

    protected function resolveProps(CallbackRegistry $registry): array
    {
        $props = $this->listItemProps;

        if ($this->leadingChangeCallback !== null) {
            $props['on_leading_change'] = $registry->register($this->leadingChangeCallback);
        }
        if ($this->trailingChangeCallback !== null) {
            $props['on_trailing_change'] = $registry->register($this->trailingChangeCallback);
        }
        if ($this->trailingPressCallback !== null) {
            $props['on_trailing_press'] = $registry->register($this->trailingPressCallback);
        }
        if ($this->swipeDeleteCallback !== null) {
            $props['on_swipe_delete'] = $registry->register($this->swipeDeleteCallback);
        }

        return $props;
    }
}
