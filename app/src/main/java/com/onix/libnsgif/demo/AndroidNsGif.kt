package com.onix.libnsgif.demo

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import com.libnsgif.NsGifLib
import java.io.ByteArrayOutputStream
import java.io.IOException

class AndroidNsGif {

    private val nsGifLib = NsGifLib.getInstance()

    fun setGif(asset: AssetManager, name: String?): Int {

        val outputStream = try {
            val outputStream = ByteArrayOutputStream()
            asset.open(name!!).copyTo(outputStream, bufferSize = 1024)
            outputStream
        } catch (e: IOException) {
            return -1
        }

//        val bitmap = Bitmap.createBitmap(0, 0, Bitmap.Config.ALPHA_8)
        val bitmap = Bitmap.createBitmap(IntArray(1), 100, 100, Bitmap.Config.ARGB_8888)
//        bitmap.setPixels()
//
//        return nsGifLib.setGif(outputStream)
        return 0
    }

    fun setGif(resource: Context, resourceId: Int): Int {
        val outputStream = try {
            val outputStream = ByteArrayOutputStream()
            resource.resources.openRawResource(resourceId).copyTo(outputStream, 1024)
            outputStream
        } catch (e: IOException) {
            return -1
        }

//        return nsGifLib.setGif(outputStream)
        return 0
    }

    fun setGif(stream: ByteArrayOutputStream): Int {
        return nsGifLib.setGif(stream.toByteArray())
    }


}