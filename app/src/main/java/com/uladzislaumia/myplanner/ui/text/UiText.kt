package com.uladzislaumia.myplanner.ui.text

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * Text produced outside of the UI (e.g. in a ViewModel) without access to a Context:
 * either a localized string resource or a ready-made message, such as one returned by a server.
 */
sealed interface UiText {
    data class Resource(
        @StringRes val id: Int,
    ) : UiText

    data class Raw(
        val value: String,
    ) : UiText
}

@Composable
fun UiText.asString(): String =
    when (this) {
        is UiText.Resource -> stringResource(id)
        is UiText.Raw -> value
    }
