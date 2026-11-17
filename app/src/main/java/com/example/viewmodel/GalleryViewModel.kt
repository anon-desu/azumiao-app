package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.media.ExifInterface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.model.GalleryRowData
import com.example.model.GalleryUiState
import com.example.model.ScrollDirection
import com.example.model.SecretPhoto
import com.example.model.fisherYatesShuffle
import com.example.model.rotateList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TOTAL_PHOTOS = 25
        private const val POST_LOAD_SETTLE_DELAY_MS = 500L
        private const val ROW_2_OFFSET = 8
        private const val ROW_3_OFFSET = 16
    }

    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Loading(progressPercent = 0))
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    init {
        loadAndCacheResources()
    }

    private fun readAssetAspectRatio(context: Context, index: Int): Float {
        return runCatching {
            val path = "images/$index.jpg"
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.assets.open(path).use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
            val orientation = context.assets.open(path).use { stream ->
                ExifInterface(stream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            }
            val swap = orientation == ExifInterface.ORIENTATION_ROTATE_90 ||
                orientation == ExifInterface.ORIENTATION_ROTATE_270 ||
                orientation == ExifInterface.ORIENTATION_TRANSPOSE ||
                orientation == ExifInterface.ORIENTATION_TRANSVERSE
            val width = if (swap) options.outHeight else options.outWidth
            val height = if (swap) options.outWidth else options.outHeight
            if (width > 0 && height > 0) width.toFloat() / height.toFloat() else 1f
        }.getOrDefault(1f)
    }

    private fun loadAndCacheResources() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            val rawPhotos = (1..TOTAL_PHOTOS).map { index ->
                SecretPhoto(
                    id = index,
                    assetUri = "file:///android_asset/images/$index.jpg",
                    aspectRatio = readAssetAspectRatio(context, index)
                )
            }
            val imageLoader = context.imageLoader
            val completedCount = AtomicInteger(0)

            rawPhotos.map { photo ->
                async {
                    runCatching {
                        val request = ImageRequest.Builder(context)
                            .data(photo.assetUri)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .build()
                        imageLoader.execute(request)
                    }
                    val done = completedCount.incrementAndGet()
                    val percent = ((done.toFloat() / TOTAL_PHOTOS) * 100f).toInt().coerceIn(0, 100)
                    _uiState.value = GalleryUiState.Loading(progressPercent = percent)
                }
            }.awaitAll()

            delay(POST_LOAD_SETTLE_DELAY_MS)

            val masterList = fisherYatesShuffle(rawPhotos)
            val rows = listOf(
                GalleryRowData(
                    rowIndex = 0,
                    direction = ScrollDirection.FORWARD,
                    photos = masterList
                ),
                GalleryRowData(
                    rowIndex = 1,
                    direction = ScrollDirection.BACKWARD,
                    photos = rotateList(masterList, ROW_2_OFFSET)
                ),
                GalleryRowData(
                    rowIndex = 2,
                    direction = ScrollDirection.FORWARD,
                    photos = rotateList(masterList, ROW_3_OFFSET)
                )
            )

            _uiState.value = GalleryUiState.Ready(rows = rows)
        }
    }

    fun onCardTapped(cardKey: String) {
        _uiState.update { current ->
            if (current is GalleryUiState.Ready) {
                val nextKey = if (current.selectedCardKey == cardKey) null else cardKey
                current.copy(selectedCardKey = nextKey)
            } else {
                current
            }
        }
    }

    fun clearSelection() {
        _uiState.update { current ->
            if (current is GalleryUiState.Ready && current.selectedCardKey != null) {
                current.copy(selectedCardKey = null)
            } else {
                current
            }
        }
    }
}
