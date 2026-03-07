package com.nativephp.plugins.compose_ui.ui

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.nativephp.mobile.ui.nativerender.NativeUIBridge
import com.nativephp.mobile.ui.nativerender.NativeUINode

object SliderRenderer {
    @Composable
    fun Render(node: NativeUINode, modifier: Modifier) {
        val p = node.props
        val initialValue = p.getFloat("value")
        val min = p.getFloat("min", 0f)
        val max = p.getFloat("max", 1f)
        val step = p.getFloat("step")
        val onChangeCb = p.getCallbackId("on_change")
        val disabled = p.getBool("disabled")

        var sliderValue by remember(node.id, initialValue) { mutableFloatStateOf(initialValue) }

        val steps = if (step > 0 && max > min) {
            ((max - min) / step).toInt() - 1
        } else {
            0
        }

        val thumbColor = p.getColor("color", 0)
        val trackColor = p.getColor("track_color", 0)

        val colors = if (thumbColor != 0 || trackColor != 0) {
            val activeColor = if (thumbColor != 0) Color(thumbColor) else SliderDefaults.colors().thumbColor
            val activeTrack = if (trackColor != 0) Color(trackColor) else SliderDefaults.colors().activeTrackColor
            SliderDefaults.colors(
                thumbColor = activeColor,
                activeTrackColor = activeTrack,
                inactiveTrackColor = activeTrack.copy(alpha = 0.3f)
            )
        } else {
            SliderDefaults.colors()
        }

        Slider(
            value = sliderValue,
            onValueChange = { newValue ->
                sliderValue = newValue
            },
            onValueChangeFinished = {
                if (onChangeCb != 0) {
                    NativeUIBridge.sendSliderChangeEvent(onChangeCb, node.id, sliderValue)
                }
            },
            modifier = modifier,
            enabled = !disabled,
            valueRange = min..max,
            steps = steps.coerceAtLeast(0),
            colors = colors
        )
    }
}
