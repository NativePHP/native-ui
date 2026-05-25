<?php

namespace Nativephp\NativeUi;

use Illuminate\Support\ServiceProvider;
use Native\Mobile\Edge\TailwindParser;
use Nativephp\NativeUi\Console\GenerateIconsCommand;

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

        // Default page layout (`<x-layouts.app>`). Devs run
        // `php artisan vendor:publish --tag=native-ui-layouts` to drop the
        // scaffold into their resources/views/components/ tree and edit
        // freely. Multiple archetypes (feed/detail/etc.) can be added by
        // copying app.blade.php to neighboring files.
        $this->publishes([
            __DIR__.'/../resources/stubs/views/components/layouts/app.blade.php'
                => resource_path('views/components/layouts/app.blade.php'),
        ], 'native-ui-layouts');

        // Load the merged config into the runtime Theme store. Consumers can
        // override with Theme::merge([...]) from their own service provider
        // after parent::boot().
        Theme::load(config('native-ui.theme', []));

        // Enable `bg-theme-*` / `text-theme-*` / `border-theme-*` Tailwind
        // classes by giving the parser a way to resolve token names against
        // our Theme. Both LIGHT and DARK resolvers are registered so the
        // parser emits a `dark` companion that the collector splits into
        // `dark_bg_color` / `dark_color` / `dark_border_color` props —
        // NodeStyleModifier picks the right hex at draw time based on
        // system colorScheme.
        TailwindParser::setThemeResolver(function (string $token): ?string {
            $value = Theme::get("light.$token");

            return is_string($value) ? $value : null;
        });
        TailwindParser::setThemeDarkResolver(function (string $token): ?string {
            $value = Theme::get("dark.$token");

            return is_string($value) ? $value : null;
        });

        if ($this->app->runningInConsole()) {
            $this->commands([
                GenerateIconsCommand::class,
            ]);
        }
    }
}