package com.libnsgif.storage

class NsGifStorage private constructor() {

    private var id: Int = 0

    fun generateId(): Int {
        return id.also {
            if (id == Int.MAX_VALUE) {
                id = 0
            } else {
                id++
            }
        }
    }

    companion object {
        private var value: NsGifStorage? = null

        fun getInstance(): NsGifStorage {
            return value ?: NsGifStorage().also { value = it }
        }
    }
}
