## nativephp/native-ui

A NativePHP Mobile plugin

### Installation

```bash
composer require nativephp/native-ui
```

### PHP Usage (Livewire/Blade)

Use the `NativeUI` facade:

@verbatim
<code-snippet name="Using NativeUI Facade" lang="php">
use Nativephp\NativeUi\Facades\NativeUI;

// Execute the plugin functionality
$result = NativeUI::execute(['option1' => 'value']);

// Get the current status
$status = NativeUI::getStatus();
</code-snippet>
@endverbatim

### Available Methods

- `NativeUI::execute()`: Execute the plugin functionality
- `NativeUI::getStatus()`: Get the current status

### Events

- `NativeUICompleted`: Listen with `#[OnNative(NativeUICompleted::class)]`

@verbatim
<code-snippet name="Listening for NativeUI Events" lang="php">
use Native\Mobile\Attributes\OnNative;
use Nativephp\NativeUi\Events\NativeUICompleted;

#[OnNative(NativeUICompleted::class)]
public function handleNativeUICompleted($result, $id = null)
{
    // Handle the event
}
</code-snippet>
@endverbatim

### JavaScript Usage (Vue/React/Inertia)

@verbatim
<code-snippet name="Using NativeUI in JavaScript" lang="javascript">
import { nativeUI } from '@nativephp/native-ui';

// Execute the plugin functionality
const result = await nativeUI.execute({ option1: 'value' });

// Get the current status
const status = await nativeUI.getStatus();
</code-snippet>
@endverbatim