package com.libnsgif.android.builder

enum class RestoreStrategy {
    // Gif will be reset and started from 0 frame
    IGNORE,
    // Last frame of gif will be restored
    LAST_FRAME
}