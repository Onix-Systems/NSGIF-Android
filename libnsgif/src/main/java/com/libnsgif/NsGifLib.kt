package com.libnsgif

import com.libnsgif.entity.NsGifInfo
import com.libnsgif.entity.NsGifResult
import com.libnsgif.storage.NsGifStorage
import java.io.InputStream

class NsGifLib private constructor() {
    private var isValid = false

    private val gifStorage = NsGifStorage.getInstance()

    init {
        System.loadLibrary("libnsgif")
    }

    fun isValid() = isValid

    fun setGif(name: String): Int {
        val id = gifStorage.generateId()
        isValid = loadGifFile(name, id) > 0
        return id
    }

    fun setGif(data: ByteArray): Int {
        val id = gifStorage.generateId()
        isValid = loadGifArray(data, id) > 0
        return id
    }

    fun setGif(stream: InputStream): Int {
        val id = gifStorage.generateId()
        isValid = loadGifStream(stream, id) > 0
        stream.close()
        return id
    }

    fun copyPixels(dest: IntArray, id: Int): Boolean {
        return getGifImageExist(dest, id) > 0
    }

    fun copyPixels(dest: IntArray, frame: Int, id: Int): Boolean {
        setGifFrame(frame, id)
        return copyPixels(dest, id)
    }

    fun getGifInfo(id: Int): NsGifInfo {
        val frames = getGifFrameCount(id)
        val delays = mutableMapOf<Int, Int>()
        repeat(frames) { index ->
            delays[index] = getGifFrameTime(index, id)
        }
        val width = getGifWidth(id)
        val height = getGifHeight(id)
        val currentFrame = getGifCurrentFrame(id)
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

    @Synchronized
    private external fun loadGifFile(name: String, id: Int): Int

    @Synchronized
    private external fun loadGifArray(array: ByteArray, id: Int): Int

    @Synchronized
    private external fun loadGifStream(stream: InputStream, id: Int): Int

    @Synchronized
    external fun destroyGif(id: Int)

    @Synchronized
    external fun getGifWidth(id: Int): Int

    @Synchronized
    external fun getGifHeight(id: Int): Int

    @Synchronized
    private external fun getGifCurrentFrame(id: Int): Int

    @Synchronized
    private external fun getGifResult(id: Int): Int

    @Synchronized
    private external fun getGifFrameCount(id: Int): Int

    @Synchronized
    private external fun getGifImageExist(dest: IntArray, id: Int): Int

    @Synchronized
    external fun setGifFrame(frame: Int, id: Int): Int

    @Synchronized
    external fun getGifFrameTime(frame: Int, id: Int): Int

    companion object {
        private var value: NsGifLib? = null

        fun getInstance(): NsGifLib {
            return value ?: NsGifLib().also { value = it }
        }
    }
}
