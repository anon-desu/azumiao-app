package com.example.model

import kotlin.random.Random

data class SecretPhoto(
    val id: Int,
    val assetUri: String,
    val aspectRatio: Float = 1f
)

enum class ScrollDirection {
    FORWARD,
    BACKWARD
}

data class GalleryRowData(
    val rowIndex: Int,
    val direction: ScrollDirection,
    val photos: List<SecretPhoto>
)

sealed interface GalleryUiState {
    data class Loading(val progressPercent: Int) : GalleryUiState

    data class Ready(
        val rows: List<GalleryRowData>,
        val selectedCardKey: String? = null
    ) : GalleryUiState {
        val selectedPhoto: SecretPhoto?
            get() {
                val key = selectedCardKey ?: return null
                return rows.firstNotNullOfOrNull { row ->
                    val prefix = "row_${row.rowIndex}_"
                    if (key.startsWith(prefix)) {
                        val id = key.removePrefix(prefix).toIntOrNull()
                        row.photos.firstOrNull { it.id == id }
                    } else {
                        null
                    }
                }
            }

        val selectedRowDirection: ScrollDirection?
            get() {
                val key = selectedCardKey ?: return null
                return rows.firstOrNull { key.startsWith("row_${it.rowIndex}_") }?.direction
            }
    }
}

/**
 * Fisher-Yates shuffle matching the original App.vue master list generation.
 */
fun <T> fisherYatesShuffle(items: List<T>, random: Random = Random.Default): List<T> {
    val mutable = items.toMutableList()
    for (i in mutable.lastIndex downTo 1) {
        val j = random.nextInt(i + 1)
        val tmp = mutable[i]
        mutable[i] = mutable[j]
        mutable[j] = tmp
    }
    return mutable.toList()
}

/**
 * Circular array rotation matching App.vue's rotateArray(arr, offset)
 * so the three staggered rows (offsets 0, 8, 16) never repeat the same photo in a column.
 */
fun <T> rotateList(items: List<T>, offset: Int): List<T> {
    if (items.isEmpty()) return items
    val k = ((offset % items.size) + items.size) % items.size
    if (k == 0) return items
    return items.drop(k) + items.take(k)
}
