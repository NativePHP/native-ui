# NativeUI Plugin for NativePHP Mobile

A NativePHP Mobile plugin

## Installation

```bash
composer require nativephp/native-ui
```

## Usage

```php
use Nativephp\NativeUi\Facades\NativeUI;

// Execute functionality
$result = NativeUI::execute(['option1' => 'value']);

// Get status
$status = NativeUI::getStatus();
```

## Listening for Events

```php
use Livewire\Attributes\On;

#[On('native:Nativephp\NativeUi\Events\NativeUICompleted')]
public function handleNativeUICompleted($result, $id = null)
{
    // Handle the event
}
```

## License

MIT