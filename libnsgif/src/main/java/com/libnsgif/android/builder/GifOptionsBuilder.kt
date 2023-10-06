package com.libnsgif.android.builder

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Matrix
import com.libnsgif.android.view.NsGifView
import java.io.ByteArrayOutputStream

class GifOptionsBuilder(private val view: NsGifView) {
    private var startOffset: Int? = null
    private var scaleType: Matrix.ScaleToFit? = null
    private var gifData: GifData = GifData.Default
    private var restoreStrategy: RestoreStrategy? = null

    fun withRestoreStrategy(strategy: RestoreStrategy): GifOptionsBuilder {
        restoreStrategy = strategy
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
        gifData = GifData.File(name)
        return this
    }

    fun withGif(asset: AssetManager, name: String): GifOptionsBuilder {
        gifData = GifData.Asset(asset, name)
        return this
    }

    fun withGif(context: Context, id: Int): GifOptionsBuilder {
        gifData = GifData.Resource(context, id)
        return this
    }

    fun withGif(data: ByteArray): GifOptionsBuilder {
        gifData = GifData.ByteArray(data)
        return this
    }

    fun withGif(stream: ByteArrayOutputStream): GifOptionsBuilder {
        gifData = GifData.ByteStream(stream)
        return this
    }

    fun build() {
        view.apply {
            scaleType?.let { setScaleType(it) }
            startOffset?.let { setStartOffset(it) }
            restoreStrategy?.let { setRestoreStrategy(it) }
        }
        parseGifData()
    }

    private fun parseGifData() {
        when (val gifData = gifData) {
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