package com.libnsgif.entity

/**
 * Enum representing the possible results of pixel data copy operations for NSGIF.
 *
 * This enum defines the various outcomes of pixel data copy operations, such as copying a GIF frame's pixels.
 * - [SUCCESS]: Indicates a successful pixel data copy operation.
 * - [NO_GIF_WITH_SUCH_ID]: Indicates that there is no NSGIF with the specified ID for the operation.
 * - [WRONG_ARRAY_SIZE]: Indicates that the destination array has the wrong size for the operation.
 *
 * @property num The numeric representation of the result.
 */
enum class NsPixelCopyResult(val num: Int) {
    SUCCESS(1), NO_GIF_WITH_SUCH_ID(-1), WRONG_ARRAY_SIZE(-2)
}