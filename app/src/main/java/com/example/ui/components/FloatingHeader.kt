package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.PeachBlush
import com.example.ui.theme.SakuraPink
import com.example.ui.theme.SoftCreamWhite
import com.example.ui.theme.SubtitlePillScrim

@Composable
fun FloatingHeader(
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "header_float")
    val floatOffsetDp by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y"
    )

    val titleStyle = if (isCompact) {
        MaterialTheme.typography.displaySmall.copy(
            fontSize = 30.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Black
        )
    } else {
        MaterialTheme.typography.displayMedium.copy(
            fontSize = 46.sp,
            lineHeight = 56.sp,
            fontWeight = FontWeight.Black
        )
    }

    val titleText = stringResource(R.string.gallery_title)
    val shadowStep: Dp = if (isCompact) 2.5.dp else 3.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .graphicsLayer {
                translationY = floatOffsetDp.dp.toPx()
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = titleText,
                style = titleStyle,
                color = PeachBlush,
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(x = shadowStep * 2, y = shadowStep * 2)
            )
            Text(
                text = titleText,
                style = titleStyle,
                color = SakuraPink,
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(x = shadowStep, y = shadowStep)
            )
            Text(
                text = titleText,
                style = titleStyle,
                color = SoftCreamWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("gallery_title")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SubtitlePillScrim)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = stringResource(R.string.gallery_subtitle),
                style = MaterialTheme.typography.titleMedium,
                color = SoftCreamWhite.copy(alpha = 0.92f),
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("gallery_subtitle")
            )
        }
    }
}
