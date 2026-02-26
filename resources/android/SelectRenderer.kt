package com.nativephp.plugins.compose_ui.ui

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode

object SelectRenderer {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val initialValue = p.getString("value")
        val placeholder = p.getString("placeholder")
        val options = p.getStringList("options")
        val onChangeCb = p.getCallbackId("on_change")
        val disabled = p.getBool("disabled")

        var expanded by remember { mutableStateOf(false) }
        var selectedValue by remember(node.id, initialValue) { mutableStateOf(initialValue) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (!disabled) expanded = it },
            modifier = modifier
        ) {
            TextField(
                value = selectedValue.ifEmpty { placeholder },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedValue = option
                            expanded = false
                            if (onChangeCb != 0) {
                                NativeUIBridge.sendSelectChangeEvent(onChangeCb, node.id, option)
                            }
                        }
                    )
                }
            }
        }
    }
}
