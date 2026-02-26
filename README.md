# ComposeUI Plugin for NativePHP Mobile

A NativePHP Mobile plugin

## Installation

```bash
composer require nativephp/compose-ui
```

## Usage

```php
use Nativephp\ComposeUi\Facades\ComposeUI;

// Execute functionality
$result = ComposeUI::execute(['option1' => 'value']);

// Get status
$status = ComposeUI::getStatus();
```

## Listening for Events

```php
use Livewire\Attributes\On;

#[On('native:Nativephp\ComposeUi\Events\ComposeUICompleted')]
public function handleComposeUICompleted($result, $id = null)
{
    // Handle the event
}
```

## License

MIT