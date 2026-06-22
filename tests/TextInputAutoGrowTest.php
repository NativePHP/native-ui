<?php

beforeEach(function () {
    $this->pluginPath = dirname(__DIR__);
});

it('exposes an auto grow text input API from PHP', function () {
    $baseInput = file_get_contents($this->pluginPath.'/src/Elements/BaseTextInput.php');
    $behavior = file_get_contents($this->pluginPath.'/src/Elements/Concerns/ConfiguresTextInputBehavior.php');

    expect($baseInput)
        ->toContain('use ConfiguresTextInputBehavior');

    expect($behavior)
        ->toContain('autoGrow')
        ->toContain("'auto_grow'")
        ->toContain("\$this->inputProps['multiline'] = true")
        ->toContain("\$this->inputProps['min_lines'] = \$minLines")
        ->toContain("\$this->inputProps['max_lines'] = \$maxLines");
});

it('accepts blade auto grow attributes', function () {
    $content = file_get_contents($this->pluginPath.'/src/Elements/BaseTextInput.php');

    expect($content)
        ->toContain("'autoGrow'")
        ->toContain("'auto-grow'");
});

it('supports auto grow in the iOS text input core', function () {
    $content = file_get_contents($this->pluginPath.'/resources/ios/NativeUITextInputCore.swift');

    expect($content)
        ->toContain('p.getBool("auto_grow")')
        ->toContain('p.getInt("min_lines")')
        ->toContain('resolvedLineRange(minLines: minLines, maxLines: maxLines)')
        ->toContain('fixedSize(horizontal: false, vertical: true)');
});

it('supports auto grow in Android text input renderers', function () {
    $shared = file_get_contents($this->pluginPath.'/resources/android/TextInputShared.kt');
    $bare = file_get_contents($this->pluginPath.'/resources/android/BareTextInputRenderer.kt');

    expect($shared)
        ->toContain('val autoGrow: Boolean')
        ->toContain('autoGrow     = p.getBool("auto_grow")')
        ->toContain('!multiline && !autoGrow')
        ->toContain('p.getBool("multiline") || p.getBool("auto_grow")');

    expect($bare)
        ->toContain('singleLine = props.singleLine')
        ->toContain('props.multiline || props.autoGrow');
});
