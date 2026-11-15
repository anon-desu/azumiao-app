package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GalleryUiState
import com.example.ui.components.AnimatedGradientBackground
import com.example.ui.components.FloatingHeader
import com.example.ui.components.LoadingOverlay
import com.example.ui.components.PhotoRibbonRow
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
                }
            }
        }

        LoadingOverlay(
            visible = isLoading,
            progressPercent = loadingState?.progressPercent ?: 100
        )
    }
}
