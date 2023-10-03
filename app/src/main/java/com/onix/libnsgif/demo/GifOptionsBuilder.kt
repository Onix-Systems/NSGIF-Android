package com.onix.libnsgif.demo

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Matrix
import java.io.ByteArrayOutputStream

class GifOptionsBuilder(private val view: GifView) {
    private var restoreFrame: Boolean = false
    private var startOffset = 0
    private var scaleType = Matrix.ScaleToFit.CENTER
    private var gif: GifData = GifData.Default

    /**
     * Set if gif should resume playing from last position,
     * after it was removed and added by Android lifecycle
     *
     * important: enabling this param will impact performance
     *
     * param: restore - to enable or disable restoring
     */
    fun withRestoreFrame(restore: Boolean): GifOptionsBuilder {
        restoreFrame = restore
        return this
    }

    fun withOffset(offset: Int): GifOptionsBuilder {
        startOffset = offset
        return this
    }

    fun withScaleType(scaleType: Matrix.ScaleToFit): GifOptionsBuilder {
        this.scaleType = scaleType
        return this
    }

    fun withGif(name: String): GifOptionsBuilder {
        gif = GifData.File(name)
        return this
    }

    fun withGif(asset: AssetManager, name: String): GifOptionsBuilder {
        gif = GifData.Asset(asset, name)
        return this
    }

    fun withGif(context: Context, id: Int): GifOptionsBuilder {
        gif = GifData.Resource(context, id)
        return this
    }

    fun withGif(data: ByteArray): GifOptionsBuilder {
        gif = GifData.ByteArray(data)
        return this
    }

    fun withGif(stream: ByteArrayOutputStream): GifOptionsBuilder {
        gif = GifData.ByteStream(stream)
        return this
    }

    fun build() {
        view.apply {
            setScaleType(scaleType)
            setStartOffset(startOffset)
            setRestoreFrames(restoreFrame)
        }
        parseGifData()
    }

    private fun parseGifData() {
        when (val gifData = gif) {
            is GifData.Asset -> view.setGif(gifData.assetManager, gifData.name)
            is GifData.ByteArray -> view.setGif(gifData.array)
            is GifData.ByteStream -> view.setGif(gifData.stream)
            is GifData.File -> view.setGif(gifData.path)
            is GifData.Resource -> view.setGif(gifData.context, gifData.id)
            GifData.Default -> return
        }
    }

    private sealed class GifData {

        class ByteStream(val stream: ByteArrayOutputStream) : GifData()

        class ByteArray(val array: kotlin.ByteArray) : GifData()

        class Resource(val context: Context, val id: Int) : GifData()

        class Asset(val assetManager: AssetManager, val name: String) : GifData()

        class File(val path: String) : GifData()

        object Default : GifData()
    }
}