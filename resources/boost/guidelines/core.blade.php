## nativephp/compose-ui

A NativePHP Mobile plugin

### Installation

```bash
composer require nativephp/compose-ui
```

### PHP Usage (Livewire/Blade)

Use the `ComposeUI` facade:

@verbatim
<code-snippet name="Using ComposeUI Facade" lang="php">
use Nativephp\ComposeUi\Facades\ComposeUI;

// Execute the plugin functionality
$result = ComposeUI::execute(['option1' => 'value']);

// Get the current status
$status = ComposeUI::getStatus();
</code-snippet>
@endverbatim

### Available Methods

- `ComposeUI::execute()`: Execute the plugin functionality
- `ComposeUI::getStatus()`: Get the current status

### Events

- `ComposeUICompleted`: Listen with `#[OnNative(ComposeUICompleted::class)]`

@verbatim
<code-snippet name="Listening for ComposeUI Events" lang="php">
use Native\Mobile\Attributes\OnNative;
use Nativephp\ComposeUi\Events\ComposeUICompleted;

#[OnNative(ComposeUICompleted::class)]
public function handleComposeUICompleted($result, $id = null)
{
    // Handle the event
}
</code-snippet>
@endverbatim

### JavaScript Usage (Vue/React/Inertia)

@verbatim
<code-snippet name="Using ComposeUI in JavaScript" lang="javascript">
import { composeUI } from '@nativephp/compose-ui';

// Execute the plugin functionality
const result = await composeUI.execute({ option1: 'value' });

// Get the current status
const status = await composeUI.getStatus();
</code-snippet>
@endverbatim