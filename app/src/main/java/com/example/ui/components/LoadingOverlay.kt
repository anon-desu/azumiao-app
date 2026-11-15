package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.LoadingMaskOverlay
import com.example.ui.theme.LoadingTrackWhite
import com.example.ui.theme.SoftCreamWhite

@Composable
fun LoadingOverlay(
    visible: Boolean,
    progressPercent: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 250)),
        exit = fadeOut(animationSpec = tween(durationMillis = 500)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LoadingMaskOverlay)
                .testTag("loading_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = SoftCreamWhite,
                    trackColor = LoadingTrackWhite,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.loading_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = SoftCreamWhite
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.loading_progress_format, progressPercent),
                    style = MaterialTheme.typography.labelMedium,
                    color = SoftCreamWhite.copy(alpha = 0.85f),
                    modifier = Modifier.testTag("loading_progress_text")
                )
            }
        }
    }
}
