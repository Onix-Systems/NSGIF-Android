package com.libnsgif

import com.libnsgif.NsGifLib.Companion.getInstance
import com.libnsgif.entity.CachingStrategy
import com.libnsgif.entity.NsGifInfo
import com.libnsgif.entity.NsGifResult
import com.libnsgif.entity.NsPixelCopyResult
import com.libnsgif.entity.exception.NoSuchGifException
import com.libnsgif.entity.exception.WrongArraySizeException
import com.libnsgif.storage.NsGifStorage
import java.io.InputStream

/**
 * The main class for working with NSGIF library.
 *
 * This class provides methods to load GIFs, manipulate frames, and retrieve information about GIFs.
 * It is designed as a singleton using the [getInstance] method.
 *
 * @see NsGifInfo
 * @see NsGifResult
 * @see NsGifStorage
 */
class NsGifLib private constructor() {

    private val gifStorage = NsGifStorage.getInstance()

    private var cachingStrategy = CachingStrategy.DISABLED

    init {
        System.loadLibrary("libnsgif")
    }

    /**
     * Checks if the GIF with provided ID is valid.
     *
     * This method verifies if the given ID is not equal to the constant [INVALID_ID].
     * An ID is considered valid if it does not match the predefined invalid ID.
     *
     * @param id The ID to check for validity.
     * @return `true` if the ID is valid, `false` otherwise.
     *
     */
    fun isValid(id: Int): Boolean {
        return id != INVALID_ID
    }

    /**
     * Sets the GIF using the provided file path.
     *
     * @param path The complete path to the GIF file.
     * @return The ID assigned to the loaded GIF.
     *
     * @sample com.libnsgif.sample.NsGifLibSample.setGifWithPath
     */
    fun setGif(path: String): Int {
        var id = gifStorage.generateId()
        id = if (loadGifFile(path, id, cachingStrategy.num) > 0) {
            id
        } else {
            INVALID_ID
        }
        if (cachingStrategy == CachingStrategy.PRE_CACHE) {
            cacheGif(id)
        }

        return id
    }

    /**
     * Sets the GIF using the provided byte array.
     *
     * @param data The byte array containing the GIF data.
     * @return The ID assigned to the loaded GIF.
     *
     * @sample com.libnsgif.sample.NsGifLibSample.setGifWithByteArray
     */
    fun setGif(data: ByteArray): Int {
        var id = gifStorage.generateId()
        id = if (loadGifArray(data, id, cachingStrategy.num) > 0) {
            id
        } else {
            INVALID_ID
        }
        if (cachingStrategy == CachingStrategy.PRE_CACHE) {
            cacheGif(id)
        }

        return id
    }

    /**
     * Sets the GIF using the provided input stream.
     *
     * The speed of loading depends on type of the stream, the fastest will be [java.io.FileInputStream],
     * so it's recommended to use it
     *
     * @param stream The input stream containing the GIF data.
     * @return The ID assigned to the loaded GIF.
     * @throws java.io.IOException If an I/O error occurs.
     *
     * @sample com.libnsgif.sample.NsGifLibSample.setGifWithInputStream
     */
    fun setGif(stream: InputStream): Int {
        var id = gifStorage.generateId()
        val result = loadGifStream(stream, id, cachingStrategy.num)
        stream.close()

        id =  if (result > 0) {
            id
        } else {
            INVALID_ID
        }
        if (cachingStrategy == CachingStrategy.PRE_CACHE) {
            cacheGif(id)
        }

        return id
    }

    /**
     * Copies the pixel data of the current GIF frame to the destination array.
     *
     * This method retrieves the pixel data of the currently displayed frame of the GIF associated with the given ID
     * and copies it into the provided destination array.
     *
     * @param dest The destination array to copy pixel data into. The size of this array should match the dimensions
     * of the GIF frame. To ensure the array size matches the frame size, use the width and height obtained from the
     * [getGifInfo] method.
     * @param id The ID of the loaded GIF.
     * @return A [Result] representing the outcome of the operation. The result can be one of the following:
     *   - [Result.success] if the operation is successful and the pixels are copied.
     *   - [Result.failure] with a [NoSuchGifException] if there is no GIF with the specified ID.
     *   - [Result.failure] with a [WrongArraySizeException] if the destination array has the wrong size.
     *   - [Result.failure] with a generic [Throwable] if an unexpected error occurs during execution.
     *
     * @see NoSuchGifException
     * @see WrongArraySizeException
     * @sample com.libnsgif.sample.NsGifLibSample.copyPixels
     */
    fun copyPixels(dest: IntArray, id: Int): Result<Unit> {
        val copyResult = getGifImageExist(dest, id)
        return when (NsPixelCopyResult.values().find { it.num == copyResult }) {
            NsPixelCopyResult.SUCCESS -> Result.success(Unit)
            NsPixelCopyResult.NO_GIF_WITH_SUCH_ID -> Result.failure(NoSuchGifException())
            NsPixelCopyResult.WRONG_ARRAY_SIZE -> Result.failure(WrongArraySizeException())
            null -> Result.failure(Throwable("Unexpected error happened during 'copyPixels' execution"))
        }
    }

    /**
     * Copies the pixel data of the specific GIF frame to the destination array.
     *
     * This method retrieves the pixel data of the currently displayed frame of the GIF associated with the given ID
     * and copies it into the provided destination array.
     *
     * @param dest The destination array to copy pixel data into. The size of this array should match the dimensions
     * of the GIF frame. To ensure the array size matches the frame size, use the width and height obtained from the
     * [getGifInfo] method.
     * @param id The ID of the loaded GIF.
     * @param frame The index of the frame to set.
     * @return A [Result] representing the outcome of the operation. The result can be one of the following:
     *   - [Result.success] if the operation is successful and the pixels are copied.
     *   - [Result.failure] with a [NoSuchGifException] if there is no GIF with the specified ID.
     *   - [Result.failure] with a [WrongArraySizeException] if the destination array has the wrong size.
     *   - [Result.failure] with a generic [Throwable] if an unexpected error occurs during execution.
     *
     * @see NoSuchGifException
     * @see WrongArraySizeException
     * @sample com.libnsgif.sample.NsGifLibSample.copyPixelsForFrame
     */
    fun copyPixels(dest: IntArray, frame: Int, id: Int): Result<Unit> {
        setGifFrame(frame, id)
        return copyPixels(dest, id)
    }

    /**
     * Retrieves information about the loaded GIF.
     *
     * @param id The ID of the loaded GIF.
     * @return An instance of [NsGifInfo] containing information about the GIF.
     *
     * @see NsGifInfo
     * @sample com.libnsgif.sample.NsGifLibSample.getGifInfo
     */
    fun getGifInfo(id: Int): NsGifInfo {
        // Get the total number of frames in the GIF
        val frames = getGifFrameCount(id)

        // Create a map to store delays for each frame
        val delays = mutableMapOf<Int, Int>()
        repeat(frames) { index ->
            delays[index] = getGifFrameTime(index, id)
        }

        // Get other information about the GIF
        val width = getGifWidth(id)
        val height = getGifHeight(id)
        val currentFrame = getGifCurrentFrame(id)

        // Get the result of loading gif
        val resultNum = getGifResult(id)
        val result = NsGifResult.values().find { it.num == resultNum } ?: NsGifResult.GIF_DATA_ERROR

        return NsGifInfo(
            frames = frames,
            delayMap = delays,
            height = height,
            width = width,
            currentFrame = currentFrame,
            result = result
        )
    }

    /**
     * Loads a GIF file into memory.
     *
     * @param name The name of the GIF file.
     * @param id The ID to assign to the loaded GIF.
     * @return The result of the operation. A positive value indicates success, while a non-positive value indicates an error.
     */
    @Synchronized
    private external fun loadGifFile(name: String, id: Int, cacheStrategy: Int): Int

    /**
     * Loads a GIF from a byte array into memory.
     *
     * @param array The byte array containing the GIF data.
     * @param id The ID to assign to the loaded GIF.
     * @return The result of the operation. A positive value indicates success, while a non-positive value indicates an error.
     */
    @Synchronized
    private external fun loadGifArray(array: ByteArray, id: Int, cacheStrategy: Int): Int

    /**
     * Loads a GIF from an input stream into memory.
     *
     * @param stream The input stream containing the GIF data.
     * @param id The ID to assign to the loaded GIF.
     * @return The result of the operation. A positive value indicates success, while a non-positive value indicates an error.
     */
    @Synchronized
    private external fun loadGifStream(stream: InputStream, id: Int, cacheStrategy: Int): Int

    /**
     * Retrieves the index of the current frame of the loaded GIF.
     *
     * @param id The ID of the loaded GIF.
     * @return The index of the current frame.
     */
    @Synchronized
    external fun getGifCurrentFrame(id: Int): Int

    /**
     * Retrieves the result code of the loaded GIF.
     *
     * @param id The ID of the loaded GIF.
     * @return The result code.
     */
    @Synchronized
    private external fun getGifResult(id: Int): Int

    /**
     * Retrieves the total number of frames in the loaded GIF.
     *
     * @param id The ID of the loaded GIF.
     * @return The total number of frames.
     */
    @Synchronized
    private external fun getGifFrameCount(id: Int): Int

    /**
     * Copies pixel data into the destination array.
     * Size of array should be the same as size of frame in pixels
     *
     * @param dest The destination array to copy pixel data into.
     * @param id The ID of the loaded GIF.
     * @return The result of the operation. A positive value indicates success, while a non-positive value indicates an error.
     */
    @Synchronized
    private external fun getGifImageExist(dest: IntArray, id: Int): Int

    /**
     * Retrieves the height of the loaded GIF.
     *
     * @param id The ID of the loaded GIF.
     * @return The height of the GIF.
     */
    @Synchronized
    external fun getGifHeight(id: Int): Int

    /**
     * Retrieves the width of the loaded GIF.
     *
     * @param id The ID of the loaded GIF.
     * @return The width of the GIF.
     */
    @Synchronized
    external fun getGifWidth(id: Int): Int

    /**
     * Sets the current frame of the loaded GIF.
     * IMPORTANT! Please be aware that GIFs are working in next pattern:
     * Each next frame is dependent on previous one, that means frame 0(starting one) is independent,
     * but frame 1 is dependent on 0, to get proper image of frame 1, frame 0 should be rendered before.
     * In code it looks like this: lets say we want to get 5th frame
     * setGifFrame(0, gifId) - not needed, frame 0 is rendered by default
     * setGifFrame(1, gifId)
     * setGifFrame(2, gifId)
     * setGifFrame(3, gifId)
     * setGifFrame(4, gifId) - here we will have frame 5 rendered correctly
     * After that we can use frame pixels, see [getGifImageExist]
     *
     * @param frame The index of the frame to set.
     * @param id The ID of the loaded GIF.
     * @return The result of the operation. A positive value indicates success, while a non-positive value indicates an error.
     */
    @Synchronized
    external fun setGifFrame(frame: Int, id: Int): Int

    /**
     * Retrieves the delay time of a specific frame in the loaded GIF.
     *
     * @param frame The index of the frame.
     * @param id The ID of the loaded GIF.
     * @return The delay time in milliseconds.
     */
    @Synchronized
    external fun getGifFrameTime(frame: Int, id: Int): Int

    @Synchronized
    external fun cacheGif(id: Int)

    /**
     * Destroys the loaded GIF and releases associated resources.
     *
     * @param id The ID of the loaded GIF to destroy.
     */
    @Synchronized
    external fun destroyGif(id: Int)

    /**
     * A singleton companion object providing access to the NSGIF library.
     *
     * This companion object is responsible for managing the singleton instance of [NsGifLib].
     * The NSGIF library can be accessed through the [getInstance] method.
     */
    companion object {
        const val INVALID_ID = -1
        private var value: NsGifLib? = null

        /**
         * Gets the singleton instance of [NsGifLib].
         *
         * @return The singleton instance.
         */
        fun getInstance(): NsGifLib {
            return value ?: NsGifLib().also { value = it }
        }

        fun initialize(cachingStrategy: CachingStrategy) {
            (value ?: getInstance()).cachingStrategy = cachingStrategy
        }
    }
}
