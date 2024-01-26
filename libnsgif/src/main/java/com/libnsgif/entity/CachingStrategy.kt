package com.libnsgif.entity

enum class CachingStrategy(val num: Int) {
    DISABLED(0),
    WHEN_DECODED(1),
    PRE_CACHE(2)
}