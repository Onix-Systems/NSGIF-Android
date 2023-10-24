package com.libnsgif.storage

import com.libnsgif.NsGifLib

/**
 * A singleton class responsible for managing the generation of unique IDs for NSGIF objects.
 *
 * This class provides a simple mechanism for generating unique identifiers (IDs) for NSGIF objects.
 * It ensures that IDs remain unique even when reaching the maximum possible integer value.
 * The class follows the singleton pattern, allowing a single instance to be shared across the application.
 *
 * Example usage:
 *
 * ```kotlin
 * val storage = NsGifStorage.getInstance()
 * val gifId1 = storage.generateId()
 * val gifId2 = storage.generateId()
 * // ...
 * ```
 *
 * @see NsGifLib
 */
internal class NsGifStorage private constructor() {

    // Current ID value
    private var id: Int = 0

    /**
     * Generates a unique ID for a GIF.
     *
     * This method generates a unique integer ID and ensures that it remains unique,
     * even when reaching the maximum possible integer value.
     *
     * @return A unique integer ID.
     */
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
        // Singleton instance of NsGifStorage
        private var value: NsGifStorage? = null

        /**
         * Gets the singleton instance of [NsGifStorage].
         *
         * @return The singleton instance of the storage.
         */
        fun getInstance(): NsGifStorage {
            return value ?: NsGifStorage().also { value = it }
        }
    }
}
