package com.libnsgif.android

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import com.libnsgif.NsGifLib
import java.io.InputStream

class NsGifAndroid private constructor() {

    private val nsGifLib = NsGifLib.getInstance()

    fun isValid(id: Int) = nsGifLib.isValid(id)

    fun setGif(data: ByteArray) = nsGifLib.setGif(data)

    fun setGif(filePath: String) = nsGifLib.setGif(filePath)

    fun setGif(stream: InputStream) = nsGifLib.setGif(stream)

    fun setGif(asset: AssetManager, name: String): Int {
        return setGif(asset.open(name))
    }

    fun setGif(resource: Context, id: Int): Int {
        return setGif(resource.resources.openRawResource(id))
    }

    fun getGifInfo(id: Int) = nsGifLib.getGifInfo(id)

    fun getGifWidth(id: Int) = nsGifLib.getGifWidth(id)

    fun getGifHeight(id: Int) = nsGifLib.getGifHeight(id)

    fun setGifFrame(frame: Int, id: Int) = nsGifLib.setGifFrame(frame, id)

    fun getGifFrameBitmap(id: Int): Bitmap {
        val width = getGifWidth(id)
        val height = getGifHeight(id)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        setupBitmap(width, height, bitmap, id)

        return bitmap
    }

    fun getGifFrame(bitmap: Bitmap, id: Int): Boolean {
        val width = getGifWidth(id)
        if (width < 0) {
            return false
        }
        val height = getGifHeight(id)

        setupBitmap(width, height, bitmap, id)

        return true
    }

    fun copyPixels(dest: IntArray, frame: Int, id: Int) = nsGifLib.copyPixels(dest, frame, id)

    fun copyPixels(dest: IntArray, id: Int) = nsGifLib.copyPixels(dest, id)

    fun destroyGif(id: Int) = nsGifLib.destroyGif(id)

    private fun setupBitmap(
        width: Int,
        height: Int,
        bitmap: Bitmap,
        id: Int,
    ) {
        val pixels = IntArray(width * height)
        if (!bitmap.isRecycled) {
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        }
        copyPixels(pixels, id)
        if (!bitmap.isRecycled) {
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        }
    }

    companion object {
        private var value: NsGifAndroid? = null

        fun getInstance(): NsGifAndroid {
            return value ?: NsGifAndroid().also { value = it }
        }
    }
}