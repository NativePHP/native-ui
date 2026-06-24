<?php

namespace Nativephp\NativeUi;

use Illuminate\Support\ServiceProvider;
use Illuminate\View\View;
use Native\Mobile\Edge\ChromeContributorRegistry;
use Native\Mobile\Edge\Element;
use Native\Mobile\Edge\Layouts\NativeLayout;
use Native\Mobile\Edge\NativeComponent;
use Native\Mobile\Edge\TailwindParser;
use Nativephp\NativeUi\Builders\Drawer;
use Nativephp\NativeUi\Console\GenerateIconsCommand;
use Nativephp\NativeUi\Elements\NativeDrawer;

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

        $this->registerLayoutDrawer();

        if ($this->app->runningInConsole()) {
            $this->commands([
                GenerateIconsCommand::class,
            ]);
        }
    }

    /**
     * Register the layout-drawer chrome contributor with core's chrome seam.
     * Core stays drawer-agnostic: it just appends whatever sentinel element a
     * contributor returns to the published tree. Here we resolve the drawer for
     * the screen (per-screen override beats the layout) and render it into a
     * `native_drawer` sentinel. The native drawer host — registered on core's
     * `NativeRootHostRegistry` from this plugin's init function — hoists it out.
     *
     * Discovery is via `method_exists`, so layouts/screens opt in by using the
     * {@see HasLayoutDrawer} / {@see InteractsWithDrawer} traits (or just by
     * declaring `drawer()` / `drawerOverride()` themselves) — core never knows.
     */
    protected function registerLayoutDrawer(): void
    {
        if (! class_exists(ChromeContributorRegistry::class)) {
            return;
        }

        ChromeContributorRegistry::register(function (NativeComponent $screen, ?NativeLayout $layout, callable $renderPartial): ?Element {
            if (method_exists($screen, 'hidesDrawer') && $screen->hidesDrawer()) {
                return null;
            }

            $builder = null;
            if (method_exists($screen, 'drawerOverride')) {
                $builder = $screen->drawerOverride();
            }
            if ($builder === null && $layout !== null && method_exists($layout, 'drawer')) {
                $builder = $layout->drawer($screen);
            }

            if (! $builder instanceof Drawer) {
                return null;
            }

            $content = $builder->getContent();
            $contentElement = $content instanceof View ? $renderPartial($content) : $content;

            $drawer = NativeDrawer::make();
            $drawer->applyAttributes([
                'mode' => $builder->getMode(),
                'width' => $builder->getWidth(),
            ]);
            $drawer->addChild($contentElement);

            return $drawer;
        });
    }
}