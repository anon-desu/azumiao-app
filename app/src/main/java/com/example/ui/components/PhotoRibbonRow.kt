package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.R
import com.example.model.GalleryRowData
import com.example.model.ScrollDirection
import com.example.model.SecretPhoto

private const val CYCLE_DURATION_SECONDS = 60f

@Composable
fun PhotoRibbonRow(
    rowData: GalleryRowData,
    isCompact: Boolean,
    selectedCardKey: String?,
    onCardTapped: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rowHeight = if (isCompact) 160.dp else 220.dp
    val baseCardSize = if (isCompact) 120.dp else 200.dp
    val expandedCardWidth = if (isCompact) 200.dp else 300.dp
    val gapSize = if (isCompact) 10.dp else 20.dp

    var hoveredCardKey by remember(rowData.rowIndex) { mutableStateOf<String?>(null) }

    val rowPrefix = "row_${rowData.rowIndex}_"
    val selectedInThisRow = selectedCardKey?.takeIf { it.startsWith(rowPrefix) }
    val activeCardKey = selectedInThisRow ?: hoveredCardKey
    val isRowPaused = hoveredCardKey != null

    val density = LocalDensity.current
    val baseSizePx = with(density) { baseCardSize.toPx() }
    val gapPx = with(density) { gapSize.toPx() }
    val expandedWidthPx = with(density) { expandedCardWidth.toPx() }
    val stepPx = baseSizePx + gapPx
    val itemCount = rowData.photos.size
    val cyclePx = (itemCount * stepPx).coerceAtLeast(1f)

    var scrollOffsetPx by remember(rowData.rowIndex, itemCount, stepPx) {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(isRowPaused, rowData.direction, cyclePx) {
        if (isRowPaused || itemCount == 0) return@LaunchedEffect
        val speedPxPerSecond = cyclePx / CYCLE_DURATION_SECONDS
        var lastNanos = withFrameNanos { it }
        while (true) {
            withFrameNanos { frameNanos ->
                val dtSeconds = ((frameNanos - lastNanos).coerceAtLeast(0L) / 1_000_000_000f)
                    .coerceAtMost(0.1f)
                lastNanos = frameNanos
                val delta = speedPxPerSecond * dtSeconds
                scrollOffsetPx = when (rowData.direction) {
                    ScrollDirection.FORWARD -> {
                        val next = scrollOffsetPx + delta
                        if (next >= cyclePx) next - cyclePx else next
                    }
                    ScrollDirection.BACKWARD -> {
                        val next = scrollOffsetPx - delta
                        if (next < 0f) next + cyclePx else next
                    }
                }
            }
        }
    }

    val restTiltAngle = when (rowData.direction) {
        ScrollDirection.FORWARD -> -18f
        ScrollDirection.BACKWARD -> 18f
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight)
            .zIndex(if (activeCardKey != null) 50f else 1f),
        contentAlignment = Alignment.CenterStart
    ) {
        val viewportWidthPx = with(density) { maxWidth.toPx() }
        val leftCutoff = -expandedWidthPx * 1.5f
        val rightCutoff = viewportWidthPx + expandedWidthPx * 1.5f

        rowData.photos.forEachIndexed { index, photo ->
            val cardKey = "${rowPrefix}${photo.id}"
            val rawCenterX = (index * stepPx) + (baseSizePx * 0.5f) - scrollOffsetPx
            var wrappedCenterX = rawCenterX % cyclePx
            if (wrappedCenterX < 0f) wrappedCenterX += cyclePx
            if (wrappedCenterX > cyclePx + leftCutoff) {
                wrappedCenterX -= cyclePx
            }

            if (wrappedCenterX in leftCutoff..rightCutoff) {
                key(cardKey) {
                    val isSelected = selectedInThisRow == cardKey
                    val isHovered = hoveredCardKey == cardKey
                    RibbonPhotoCard(
                        photo = photo,
                        cardKey = cardKey,
                        testTag = "photo_card_${rowData.rowIndex}_${photo.id}",
                        centerX = wrappedCenterX,
                        baseSize = baseCardSize,
                        expandedWidth = expandedCardWidth,
                        restTiltAngle = restTiltAngle,
                        isCompact = isCompact,
                        isHovered = isHovered,
                        isSelected = isSelected,
                        onHoverChanged = { hovered ->
                            hoveredCardKey = if (hovered) {
                                cardKey
                            } else if (hoveredCardKey == cardKey) {
                                null
                            } else {
                                hoveredCardKey
                            }
                        },
                        onTapped = {
                            hoveredCardKey = null
                            onCardTapped(cardKey)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RibbonPhotoCard(
    photo: SecretPhoto,
    cardKey: String,
    testTag: String,
    centerX: Float,
    baseSize: Dp,
    expandedWidth: Dp,
    restTiltAngle: Float,
    isCompact: Boolean,
    isHovered: Boolean,
    isSelected: Boolean,
    onHoverChanged: (Boolean) -> Unit,
    onTapped: () -> Unit
) {
    val isActive = isHovered || isSelected
    val targetWidth = if (isHovered && !isSelected) expandedWidth else baseSize
    val animatedWidth by animateDpAsState(
        targetValue = targetWidth,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "card_width"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (isHovered && !isSelected) (if (isCompact) 1.08f else 1.1f) else 1f,
        animationSpec = tween(durationMillis = 420),
        label = "card_scale"
    )
    val animatedRotationY by animateFloatAsState(
        targetValue = if (isActive) 0f else restTiltAngle,
        animationSpec = tween(durationMillis = 420),
        label = "card_rotation_y"
    )
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.82f,
        animationSpec = tween(durationMillis = 320),
        label = "card_alpha"
    )
    val animatedElevation by animateDpAsState(
        targetValue = if (isActive) 20.dp else 6.dp,
        animationSpec = tween(durationMillis = 380),
        label = "card_elevation"
    )

    val density = LocalDensity.current
    val widthPx = with(density) { animatedWidth.toPx() }
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    val imageRequest = remember(photo.assetUri) {
        ImageRequest.Builder(context)
            .data(photo.assetUri)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .crossfade(false)
            .build()
    }

    val shape = RoundedCornerShape(15.dp)

    Surface(
        shape = shape,
        color = Color.Transparent,
        shadowElevation = animatedElevation,
        modifier = Modifier
            .zIndex(if (isActive) 200f else 1f)
            .graphicsLayer {
                translationX = centerX - (widthPx * 0.5f)
                scaleX = animatedScale
                scaleY = animatedScale
                rotationY = animatedRotationY
                alpha = animatedAlpha
                cameraDistance = 3.8f
            }
            .minimumInteractiveComponentSize()
            .size(width = animatedWidth, height = baseSize)
            .pointerInput(cardKey) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val isMouse = event.changes.any { it.type == PointerType.Mouse }
                        if (isMouse) {
                            when (event.type) {
                                PointerEventType.Enter -> onHoverChanged(true)
                                PointerEventType.Exit -> onHoverChanged(false)
                            }
                        }
                    }
                }
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onTapped
            )
            .testTag(testTag)
    ) {
        AsyncImage(
            model = imageRequest,
            contentDescription = stringResource(R.string.photo_content_description, photo.id),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
