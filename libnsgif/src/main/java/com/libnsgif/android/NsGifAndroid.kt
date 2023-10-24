package com.libnsgif.android

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import com.libnsgif.NsGifLib
import com.libnsgif.entity.NsGifInfo
import com.libnsgif.entity.exception.NoSuchGifException
import com.libnsgif.entity.exception.WrongArraySizeException
import java.io.InputStream

/**
 * A class that provides Android-specific functionality for working with GIF images.
 *
 * This class serves as an Android-specific wrapper for NSGIF operations. It enables loading and manipulating GIFs
 * in an Android application. It leverages the core functionality provided by [NsGifLib] to work with GIF data and
 * offers additional Android-specific methods for handling GIFs, such as generating [Bitmap] objects from GIF frames.
 *
 * @see NsGifLib
 * @see Bitmap
 *
 */
class NsGifAndroid private constructor() {

    private val nsGifLib = NsGifLib.getInstance()

    /**
     * Checks if the provided ID is valid for a loaded GIF.
     *
     * This method validates whether the given ID corresponds to a successfully loaded GIF object.
     *
     * @param id The ID to check for validity.
     * @return `true` if the ID is valid, `false` otherwise.
     */
    fun isValid(id: Int) = nsGifLib.isValid(id)

    /**
     * Loads a GIF from a byte array into memory.
     *
     * @param data The byte array containing the GIF data.
     * @return The ID of the loaded GIF.
     */
    fun setGif(data: ByteArray) = nsGifLib.setGif(data)

    /**
     * Loads a GIF from a file path into memory.
     *
     * @param filePath The file path to the GIF.
     * @return The ID of the loaded GIF.
     */
    fun setGif(filePath: String) = nsGifLib.setGif(filePath)

    /**
     * Loads a GIF from an input stream into memory.
     *
     * @param stream The input stream containing the GIF data.
     * @return The ID of the loaded GIF.
     */
    fun setGif(stream: InputStream) = nsGifLib.setGif(stream)

    /**
     * Loads a GIF from an Android asset using its name.
     *
     * @param asset The [AssetManager] for accessing Android assets.
     * @param name The name of the asset (GIF file).
     * @return The ID of the loaded GIF.
     */
    fun setGif(asset: AssetManager, name: String): Int {
        return setGif(asset.open(name))
    }

    /**
     * Loads a GIF from an Android resource using its resource ID.
     *
     * @param resource The Android [Context] for accessing resources.
     * @param id The resource ID of the GIF.
     * @return The ID of the loaded GIF.
     */
    fun setGif(resource: Context, id: Int): Int {
        return setGif(resource.resources.openRawResource(id))
    }

    /**
     * Retrieves information about the loaded NSGIF object.
     *
     * This method fetches information about the NSGIF object associated with the given ID.
     *
     * @param id The ID of the loaded NSGIF.
     * @return An instance of [NsGifInfo] containing details about the NSGIF.
     */
    fun getGifInfo(id: Int) = nsGifLib.getGifInfo(id)

    /**
     * Retrieves the width of the NSGIF image associated with the given ID.
     *
     * This method returns the width (in pixels) of the NSGIF image corresponding to the provided ID.
     *
     * @param id The ID of the loaded NSGIF.
     * @return The width of the NSGIF image.
     */
    fun getGifWidth(id: Int) = nsGifLib.getGifWidth(id)

    /**
     * Retrieves the height of the NSGIF image associated with the given ID.
     *
     * This method returns the height (in pixels) of the NSGIF image corresponding to the provided ID.
     *
     * @param id The ID of the loaded NSGIF.
     * @return The height of the NSGIF image.
     */
    fun getGifHeight(id: Int) = nsGifLib.getGifHeight(id)

    /**
     * Sets the current frame of the loaded NSGIF.
     *
     * This method updates the currently displayed frame of the NSGIF object associated with the given ID.
     *
     * @param frame The index of the frame to set.
     * @param id The ID of the loaded NSGIF.
     * @return The result of the operation. A positive value indicates success, while a non-positive value indicates an error.
     */
    fun setGifFrame(frame: Int, id: Int) = nsGifLib.setGifFrame(frame, id)

    /**
     * Generates a [Bitmap] representing the current frame of the NSGIF.
     *
     * This method creates a [Bitmap] object that visually represents the currently displayed frame
     * of the NSGIF associated with the given ID.
     *
     * @param id The ID of the loaded NSGIF.
     * @return A [Bitmap] object representing the current frame of the NSGIF.
     */
    fun getGifFrameBitmap(id: Int): Bitmap {
        val width = getGifWidth(id)
        val height = getGifHeight(id)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        copyToBitmap(width, height, bitmap, id)

        return bitmap
    }

    /**
     * Retrieves the pixel data of the current frame of the NSGIF and populates the provided [Bitmap].
     *
     * This method extracts the pixel data of the currently displayed frame of the NSGIF and fills
     * the provided [Bitmap] with the frame's image.
     *
     * @param bitmap The [Bitmap] to populate with the current frame.
     * @param id The ID of the loaded NSGIF.
     * @return `true` if the operation was successful, `false` if there was an error.
     */
    fun getGifFrame(bitmap: Bitmap, id: Int): Boolean {
        val width = getGifWidth(id)
        if (width < 0) {
            return false
        }
        val height = getGifHeight(id)

        return copyToBitmap(width, height, bitmap, id)
    }

    /**
     * Copies the pixel data of a specific frame of the NSGIF to the provided destination array.
     *
     * This method copies the pixel data of the specified frame of the NSGIF associated with the given ID
     * into the provided destination array. The destination array should have the same dimensions as the
     * frame in pixels to ensure proper copying.
     *
     * @param dest The destination array to store the pixel data. It must have the same dimensions (width * height)
     * as the GIF frame.
     * @param frame The index of the frame to copy.
     * @param id The ID of the loaded NSGIF.
     * @return A [Result] representing the outcome of the operation. The result can be one of the following:
     *   - [Result.success] if the operation is successful.
     *   - [Result.failure] with a [NoSuchGifException] if there is no NSGIF with the specified ID.
     *   - [Result.failure] with a [WrongArraySizeException] if the destination array has the wrong size.
     *   - [Result.failure] with a generic [Throwable] if an unexpected error occurs during execution.
     *
     * @see NsGifLib.copyPixels
     */
    fun copyPixels(dest: IntArray, frame: Int, id: Int) = nsGifLib.copyPixels(dest, frame, id)

    /**
     * Copies the pixel data of the current frame of the NSGIF to the provided destination array.
     *
     * This method copies the pixel data of the currently displayed frame of the NSGIF associated with the given ID
     * into the provided destination array. The destination array should have the same dimensions as the
     * frame in pixels to ensure proper copying.
     *
     * @param dest The destination array to store the pixel data. It must have the same dimensions (width * height)
     * as the GIF frame.
     * @param id The ID of the loaded NSGIF.
     * @return A [Result] representing the outcome of the operation. The result can be one of the following:
     *   - [Result.success] if the operation is successful.
     *   - [Result.failure] with a [NoSuchGifException] if there is no NSGIF with the specified ID.
     *   - [Result.failure] with a [WrongArraySizeException] if the destination array has the wrong size.
     *   - [Result.failure] with a generic [Throwable] if an unexpected error occurs during execution.
     *
     * @see NsGifLib.copyPixels
     */
    fun copyPixels(dest: IntArray, id: Int) = nsGifLib.copyPixels(dest, id)

    /**
     * Destroys the loaded GIF object associated with the given ID.
     *
     * This method releases the resources associated with the loaded GIF object and invalidates the ID.
     *
     * @param id The ID of the loaded NSGIF to destroy.
     */
    fun destroyGif(id: Int) = nsGifLib.destroyGif(id)

    private fun copyToBitmap(
        width: Int,
        height: Int,
        bitmap: Bitmap,
        id: Int,
    ): Boolean {
        val pixels = IntArray(width * height)
        bitmap.ensureNotRecycled {
            it.getPixels(pixels, 0, width, 0, 0, width, height)
        }
        return if (copyPixels(pixels, id).isSuccess) {
            bitmap.ensureNotRecycled {
                it.setPixels(pixels, 0, width, 0, 0, width, height)
            }
            true
        } else {
            false
        }
    }

    private fun Bitmap.ensureNotRecycled(action: (Bitmap) -> Unit) {
        if (!isRecycled) {
            action(this)
        }
    }

    /**
     * Gets a singleton instance of [NsGifAndroid].
     *
     * @return The singleton instance for Android-specific NSGIF functionality.
     */
    companion object {
        // Singleton instance of NsGifAndroid
        private var value: NsGifAndroid? = null

        /**
         * Gets the singleton instance of [NsGifAndroid].
         *
         * @return The singleton instance.
         */
        fun getInstance(): NsGifAndroid {
            return value ?: NsGifAndroid().also { value = it }
        }
    }
}