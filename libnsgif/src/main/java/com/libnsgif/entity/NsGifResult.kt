package com.libnsgif.entity

/**
 * An enumeration representing the result codes for loading GIF into memory.
 *
 * This enumeration defines result codes that can be returned when working with GIF loading.
 * It provides meaningful values for different outcomes, such as success or various error conditions.
 *
 * @property num The numeric code associated with each result.
 *
 * Example usage:
 *
 * ```kotlin
 * val gifInfo = NsGifLib.getInstance().getGifInfo(gifId)
 * val result = gifInfo.result
 * if (result == NsGifResult.GIF_OK) {
 *     println("The operation was successful.")
 * }
 * ```
 */
enum class NsGifResult(val num: Int) {
    GIF_WORKING(1),
    GIF_OK(0),
    GIF_INSUFFICIENT_FRAME_DATA(-1),
    GIF_FRAME_DATA_ERROR(-2),
    GIF_INSUFFICIENT_DATA(-3),
    GIF_DATA_ERROR(-4),
    GIF_INSUFFICIENT_MEMORY(-5),
    GIF_FRAME_NO_DISPLAY(-6),
    GIF_END_OF_FRAME(-7)
}
