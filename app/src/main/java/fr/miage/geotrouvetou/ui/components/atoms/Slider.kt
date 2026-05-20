package fr.miage.geotrouvetou.ui.components.atoms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.miage.geotrouvetou.R
import kotlin.math.roundToInt

private val ThumbRadius = 14.dp

@Composable
private fun SliderThumb(color: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(28.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(2.5.dp, Color.White, CircleShape)
                .background(color, CircleShape)
        )
    }
}

@Composable
private fun ValueLabels(
    fractions: List<Float>,
    labels: List<String>,
    color: Color,
) {
    Layout(
        modifier = Modifier.fillMaxWidth(),
        content = {
            labels.forEach { label ->
                Text(label, fontWeight = FontWeight.Bold, color = color, fontSize = 16.sp)
            }
        }
    ) { measurables, constraints ->
        val thumbPx = ThumbRadius.roundToPx()
        val usablePx = constraints.maxWidth - 2 * thumbPx
        val placeables = measurables.map { it.measure(Constraints()) }
        layout(constraints.maxWidth, placeables.maxOf { it.height }) {
            placeables.forEachIndexed { i, placeable ->
                placeable.placeRelative(
                    x = thumbPx + (usablePx * fractions[i]).roundToInt() - placeable.width / 2,
                    y = 0
                )
            }
        }
    }
}

@Composable
private fun RangeLabels(
    valueRange: ClosedFloatingPointRange<Float>,
    color: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ThumbRadius),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(valueRange.start.toInt().toString(), color = color, fontSize = 12.sp)
        Text(((valueRange.start + valueRange.endInclusive) / 2).toInt().toString(), color = color, fontSize = 12.sp)
        Text(valueRange.endInclusive.toInt().toString(), color = color, fontSize = 12.sp)
    }
}

@Composable
private fun SliderTag(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = colorResource(R.color.primary_transparent),
                shape = RoundedCornerShape(50),
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = colorResource(R.color.primary_600),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun SliderHeader(
    label: String?,
    required: Boolean,
    tagText: String?,
) {
    if (label == null && tagText == null) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (label != null) Arrangement.SpaceBetween else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (label != null) {
            Text(
                text = buildAnnotatedString {
                    append(label)
                    if (required) {
                        withStyle(SpanStyle(color = colorResource(R.color.danger_500))) {
                            append(" *")
                        }
                    }
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(R.color.text_darker),
            )
        }
        if (tagText != null) {
            SliderTag(tagText)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleTrack(
    sliderState: SliderState,
    activeColor: Color,
    inactiveColor: Color,
) {
    val fraction = (sliderState.value - sliderState.valueRange.start) /
                   (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
    ) {
        val r = size.height / 2
        drawRoundRect(color = inactiveColor, cornerRadius = CornerRadius(r))
        drawRoundRect(
            color = activeColor,
            size = Size(size.width * fraction, size.height),
            cornerRadius = CornerRadius(r)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangeTrack(
    state: RangeSliderState,
    activeColor: Color,
    inactiveColor: Color,
) {
    val start = (state.activeRangeStart - state.valueRange.start) /
                (state.valueRange.endInclusive - state.valueRange.start)
    val end = (state.activeRangeEnd - state.valueRange.start) /
              (state.valueRange.endInclusive - state.valueRange.start)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
    ) {
        val r = size.height / 2
        drawRoundRect(color = inactiveColor, cornerRadius = CornerRadius(r))
        drawRect(
            color = activeColor,
            topLeft = Offset(size.width * start, 0f),
            size = Size(size.width * (end - start), size.height)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    thumbColor: Color? = null,
    activeTrackColor: Color? = null,
    inactiveTrackColor: Color? = null,
    activeTickColor: Color? = null,
    inactiveTickColor: Color? = null,
    label: String? = null,
    required: Boolean = false,
    unit: String? = null,
    showValueLabel: Boolean = false,
    showRangeLabels: Boolean = false,
) {
    val primary = colorResource(R.color.primary_500)
    val inactive = colorResource(R.color.text_disabled)
    val resolvedThumb = thumbColor ?: primary
    val resolvedActive = activeTrackColor ?: primary
    val resolvedInactive = inactiveTrackColor ?: inactive

    val valueInt = value.toInt()

    Column(modifier = modifier.fillMaxWidth()) {
        SliderHeader(
            label = label,
            required = required,
            tagText = unit?.let { "$valueInt $it" },
        )
        if (showValueLabel) {
            val fraction = ((value - valueRange.start) /
                           (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
            ValueLabels(
                fractions = listOf(fraction),
                labels = listOf(valueInt.toString()),
                color = colorResource(R.color.text_darker),
            )
        }

        androidx.compose.material3.Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
            onValueChangeFinished = onValueChangeFinished,
            colors = SliderDefaults.colors(
                activeTickColor = activeTickColor ?: resolvedActive,
                inactiveTickColor = inactiveTickColor ?: resolvedInactive,
            ),
            thumb = { SliderThumb(color = resolvedThumb) },
            track = { state -> SingleTrack(state, resolvedActive, resolvedInactive) }
        )

        if (showRangeLabels) {
            RangeLabels(valueRange = valueRange, color = inactive)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    thumbColor: Color? = null,
    activeTrackColor: Color? = null,
    inactiveTrackColor: Color? = null,
    activeTickColor: Color? = null,
    inactiveTickColor: Color? = null,
    label: String? = null,
    required: Boolean = false,
    unit: String? = null,
    showValueLabels: Boolean = false,
    showRangeLabels: Boolean = false,
) {
    val primary = colorResource(R.color.primary_600)
    val inactive = colorResource(R.color.text_disabled)
    val resolvedThumb = thumbColor ?: primary
    val resolvedActive = activeTrackColor ?: primary
    val resolvedInactive = inactiveTrackColor ?: inactive

    Column(modifier = modifier.fillMaxWidth()) {
        val startInt = value.start.toInt()
        val endInt = value.endInclusive.toInt()
        SliderHeader(
            label = label,
            required = required,
            tagText = unit?.let { "$startInt - $endInt $it" },
        )
        if (showValueLabels) {
            val range = valueRange.endInclusive - valueRange.start
            val startFrac = ((value.start - valueRange.start) / range).coerceIn(0f, 1f)
            val endFrac = ((value.endInclusive - valueRange.start) / range).coerceIn(0f, 1f)
            ValueLabels(
                fractions = listOf(startFrac, endFrac),
                labels = listOf(startInt.toString(), endInt.toString()),
                color = colorResource(R.color.text_darker),
            )
        }

        androidx.compose.material3.RangeSlider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
            onValueChangeFinished = onValueChangeFinished,
            colors = SliderDefaults.colors(
                activeTickColor = activeTickColor ?: resolvedActive,
                inactiveTickColor = inactiveTickColor ?: resolvedInactive,
            ),
            startThumb = { SliderThumb(color = resolvedThumb) },
            endThumb = { SliderThumb(color = resolvedThumb) },
            track = { state -> RangeTrack(state, resolvedActive, resolvedInactive) }
        )

        if (showRangeLabels) {
            RangeLabels(valueRange = valueRange, color = inactive)
        }
    }
}

@Composable
private fun PreviewWrapper(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .background(colorResource(R.color.background))
            .padding(16.dp)
    ) { content() }
}

@Preview(name = "Slider – Default")
@Composable
private fun SliderDefaultPreview() {
    PreviewWrapper {
        Slider(value = 0.5f, onValueChange = {})
    }
}

@Preview(name = "Slider – With Labels")
@Composable
private fun SliderWithLabelsPreview() {
    PreviewWrapper {
        Slider(value = 50f, onValueChange = {}, valueRange = 0f..100f, showValueLabel = true, showRangeLabels = true)
    }
}

@Preview(name = "RangeSlider – Default")
@Composable
private fun RangeSliderDefaultPreview() {
    PreviewWrapper {
        RangeSlider(value = 10f..25f, onValueChange = {}, valueRange = 0f..100f)
    }
}

@Preview(name = "RangeSlider – With Labels")
@Composable
private fun RangeSliderWithLabelsPreview() {
    PreviewWrapper {
        RangeSlider(value = 10f..25f, onValueChange = {}, valueRange = 0f..100f, showValueLabels = true, showRangeLabels = true)
    }
}

@Preview(name = "RangeSlider – Label Required")
@Composable
private fun RangeSliderLabelRequiredPreview() {
    PreviewWrapper {
        RangeSlider(
            value = 10f..25f,
            onValueChange = {},
            valueRange = 0f..100f,
            label = "Distance de recherche",
            required = true,
            showValueLabels = true,
            showRangeLabels = true
        )
    }
}

@Preview(name = "RangeSlider – With Unit")
@Composable
private fun RangeSliderWithUnitPreview() {
    PreviewWrapper {
        RangeSlider(
            value = 10f..25f,
            onValueChange = {},
            valueRange = 0f..100f,
            label = "Distance de recherche",
            required = true,
            unit = "km",
            showValueLabels = true,
            showRangeLabels = true
        )
    }
}
