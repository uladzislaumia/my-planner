package com.uladzislaumia.myplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uladzislaumia.myplanner.domain.model.User
import com.uladzislaumia.myplanner.ui.text.initial
import com.uladzislaumia.myplanner.ui.theme.MyPlannerTheme

/**
 * Round avatar with the user's initial.
 * Screen readers read [contentDescription] instead of the letter.
 */
@Composable
fun UserAvatar(
    user: User?,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    contentDescription: String? = null,
) {
    val locale = LocalConfiguration.current.locales[0]
    val initial = user?.initial(locale).orEmpty()

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .size(size)
                .clearAndSetSemantics { contentDescription?.let { this.contentDescription = it } }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Text(
            text = initial,
            // The letter takes a fixed share of the circle, so the avatar scales as a whole
            fontSize = (size.value * INITIAL_SIZE_RATIO).sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

private const val INITIAL_SIZE_RATIO = 0.45f

@Preview
@Composable
fun UserAvatarPreview() {
    MyPlannerTheme {
        UserAvatar(user = User(id = "1", name = "Anna", email = "anna@example.com"))
    }
}
