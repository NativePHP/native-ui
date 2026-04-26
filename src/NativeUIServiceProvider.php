<?php

namespace Nativephp\NativeUi;

use Illuminate\Support\ServiceProvider;
use Native\Mobile\Edge\TailwindParser;

class NativeUIServiceProvider extends ServiceProvider
{
    public function register(): void
    {
        $this->mergeConfigFrom(
            __DIR__.'/../config/native-ui.php',
            'native-ui'
        );
    }

    public function boot(): void
    {
        $this->publishes([
            __DIR__.'/../config/native-ui.php' => config_path('native-ui.php'),
        ], 'native-ui-config');

        // Load the merged config into the runtime Theme store. Consumers can
        // override with Theme::merge([...]) from their own service provider
        // after parent::boot().
        Theme::load(config('native-ui.theme', []));

        // Enable `bg-theme-*` / `text-theme-*` Tailwind classes by giving
        // the parser a way to resolve token names against our Theme.
        //
        // Resolves against the LIGHT token set — native-side components
        // (<native:screen> etc.) handle dark-mode switching themselves.
        TailwindParser::setThemeResolver(function (string $token): ?string {
            $value = Theme::get("light.$token");

            return is_string($value) ? $value : null;
        });
    }
}