package com.uladzislaumia.myplanner.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.colorResource
import com.uladzislaumia.myplanner.R

/** Theme background with two soft sandy hills along the bottom edge. */
@Composable
fun Modifier.warmBackground(): Modifier {
    val background = MaterialTheme.colorScheme.background
    val hillFar = colorResource(R.color.hill_far)
    val hillNear = colorResource(R.color.hill_near)
    return drawBehind {
        drawRect(background)
        drawHill(hillFar, top = 0.78f, peak = 0.25f, crestHeight = 0.06f)
        drawHill(hillNear, top = 0.86f, peak = 0.7f, crestHeight = 0.05f)
    }
}

/**
 * A hill from the left to the right edge: [top] is where it meets the edges and [peak] is the horizontal
 * position of its crest, both as fractions of the size; [crestHeight] is how much the crest rises above [top].
 */
private fun DrawScope.drawHill(
    color: Color,
    top: Float,
    peak: Float,
    crestHeight: Float,
) {
    val baseY = size.height * top
    val crestY = baseY - size.height * crestHeight
    val crestX = size.width * peak
    val path =
        Path().apply {
            moveTo(0f, baseY)
            quadraticTo(crestX / 2, crestY, crestX, crestY)
            quadraticTo(crestX + (size.width - crestX) / 2, crestY, size.width, baseY)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
    drawPath(path, color)
}
