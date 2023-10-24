package com.libnsgif.entity

/**
 * A data class representing information about a GIF object.
 *
 * This data class encapsulates various properties related to a GIF, including the number of frames,
 * a map of frame-to-delay time, the height and width of the GIF, the current decoded frame, and the result
 * of decoding.
 *
 * @property frames The total number of frames in the GIF.
 * @property delayMap A map that associates each frame index with its delay time in milliseconds.
 * @property height The height of the GIF image.
 * @property width The width of the GIF image.
 * @property currentFrame The index of the currently decoded frame.
 * @property result The result of decoding, represented as a [NsGifResult] enum.
 *
 * Example usage:
 *
 * ```kotlin
 * val gifInfo = NsGifLib.getInstance().getGifInfo(gifId)
 *
 * println("Total frames: ${gifInfo.frames}")
 * println("Current frame: ${gifInfo.currentFrame}")
 * println("Result: ${gifInfo.result}")
 * ```
 *
 * @see NsGifResult
 */
data class NsGifInfo(
    /* frames count */
    val frames: Int = 0,
    /* map of frame - delay */
    val delayMap: Map<Int, Int> = mapOf(),
    /* gif height */
    val height: Int = 0,
    /* gif width */
    val width: Int = 0,
    /* current decoded frame */
    val currentFrame: Int = 0,
    /* result of decoding */
    val result: NsGifResult = NsGifResult.GIF_FRAME_NO_DISPLAY
)
