package com.uladzislaumia.myplanner.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uladzislaumia.myplanner.ui.theme.MyPlannerTheme

/**
 * Task title as the heading of a task sheet: large bold text without a frame, wrapping onto a few lines.
 * Line breaks are turned into spaces, since a title is one line of meaning.
 */
@Composable
fun TaskTitleField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    focusRequester: FocusRequester? = null,
) {
    val colorScheme = MaterialTheme.colorScheme
    val textStyle =
        MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface,
        )

    Column(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it.replace('\n', ' ')) },
            textStyle = textStyle,
            maxLines = MAX_TITLE_LINES,
            cursorBrush = SolidColor(colorScheme.primary),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = textStyle,
                            color = colorScheme.onSurfaceVariant.copy(alpha = PLACEHOLDER_ALPHA),
                        )
                    }
                    innerTextField()
                }
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
        )
        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.error,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

private const val MAX_TITLE_LINES = 3
private const val PLACEHOLDER_ALPHA = 0.6f

@Preview(showBackground = true)
@Composable
fun TaskTitleFieldPreview() {
    MyPlannerTheme {
        TaskTitleField(value = "", onValueChange = {}, placeholder = "New Task")
    }
}
