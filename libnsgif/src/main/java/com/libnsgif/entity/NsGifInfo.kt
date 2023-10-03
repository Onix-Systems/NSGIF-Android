package com.libnsgif.entity

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
