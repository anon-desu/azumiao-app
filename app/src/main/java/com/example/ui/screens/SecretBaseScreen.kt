package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.model.GalleryUiState
import com.example.ui.components.AnimatedGradientBackground
import com.example.ui.components.FloatingHeader
import com.example.ui.components.LoadingOverlay
import com.example.ui.components.PhotoRibbonRow
import com.example.ui.theme.SoftCreamWhite
import com.example.ui.theme.SubtitlePillScrim
import com.example.viewmodel.GalleryViewModel

@Composable
fun SecretBaseScreen(
    viewModel: GalleryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val readyState = uiState as? GalleryUiState.Ready
    val loadingState = uiState as? GalleryUiState.Loading
    val isLoading = loadingState != null
    val selectedCardKey = readyState?.selectedCardKey

    BackHandler(enabled = selectedCardKey != null) {
        viewModel.clearSelection()
    }

    val clearInteractionSource = remember { MutableInteractionSource() }
    val uriHandler = LocalUriHandler.current
    val websiteUrl = stringResource(R.string.website_link_url)

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedGradientBackground()

        if (readyState != null) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        enabled = selectedCardKey != null,
                        interactionSource = clearInteractionSource,
                        indication = null,
                        onClick = { viewModel.clearSelection() }
                    )
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .then(if (isLoading) Modifier.blur(20.dp) else Modifier)
            ) {
                val isCompact = maxWidth < 768.dp
                val viewportHeight = maxHeight
                val headerBottomSpacing = if (isCompact) 24.dp else 40.dp
                val rowGap = if (isCompact) 10.dp else 20.dp
                val footerTopSpacing = if (isCompact) 24.dp else 36.dp

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = viewportHeight)
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    FloatingHeader(isCompact = isCompact)

                    Spacer(modifier = Modifier.height(headerBottomSpacing))

                    readyState.rows.forEachIndexed { index, rowData ->
                        if (index > 0) {
                            Spacer(modifier = Modifier.height(rowGap))
                        }
                        PhotoRibbonRow(
                            rowData = rowData,
                            isCompact = isCompact,
                            selectedCardKey = readyState.selectedCardKey,
                            onCardTapped = viewModel::onCardTapped,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(footerTopSpacing))

                    val pillShape = RoundedCornerShape(20.dp)
                    Surface(
                        shape = pillShape,
                        color = SubtitlePillScrim,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(pillShape)
                            .clickable {
                                runCatching { uriHandler.openUri(websiteUrl) }
                            }
                            .testTag("website_link")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_open_link),
                                contentDescription = stringResource(R.string.website_link_content_description),
                                tint = SoftCreamWhite.copy(alpha = 0.92f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.website_link_label),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    textDecoration = TextDecoration.Underline
                                ),
                                color = SoftCreamWhite.copy(alpha = 0.95f)
                            )
                        }
                    }
                }
            }
        }

        LoadingOverlay(
            visible = isLoading,
            progressPercent = loadingState?.progressPercent ?: 100
        )
    }
}
