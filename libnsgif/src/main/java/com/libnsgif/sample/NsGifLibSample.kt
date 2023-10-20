package com.libnsgif.sample

import com.libnsgif.NsGifLib
import java.io.FileInputStream

/**
 * Not for usage, just simple class for examples of usage [com.libnsgif.NsGifLib] methods
 */
@Suppress("unused", "unused_expression")
internal class NsGifLibSample {
    private val nsGifLib = NsGifLib.getInstance()

    private fun setGifWithPath() {
        val filePath = "path/to/file/gifAnimation.gif"
        val id = nsGifLib.setGif(filePath)
        if (nsGifLib.isValid(id)) {
            // process gif, obtain information or copy pixels
            id
        } else {
            // provide retry policy or simply handle this case
        }
    }

    private fun setGifWithByteArray() {
        val array = ByteArray(0)
        val id = nsGifLib.setGif(array)
        if (nsGifLib.isValid(id)) {
            // process gif, obtain information or copy pixels
            id
        } else {
            // provide retry policy or simply handle this case
        }
    }

    private fun setGifWithInputStream() {
        // as example used fastest variant - FileInputStream
        val stream = FileInputStream("path/to/file/gifAnimation.gif")
        val id = nsGifLib.setGif(stream)
        // you don't have to close stream, it will be closed inside lib
        if (nsGifLib.isValid(id)) {
            // process gif, obtain information or copy pixels
            id
        } else {
            // provide retry policy or simply handle this case
        }
    }

    private fun copyPixels() {
        val gifId = 0   // should be obtained from setGif method
        val height = nsGifLib.getGifHeight(gifId)
        val width = nsGifLib.getGifWidth(gifId)
        // array of pixels
        val destinationArray = IntArray(width * height)

        nsGifLib.copyPixels(destinationArray, gifId)

        // now you can use destinationArray, it will contain pixels of current gif frame
    }

    private fun copyPixelsForFrame() {
        val gifId = 0   // should be obtained from setGif method
        val gifInfo = nsGifLib.getGifInfo(gifId)
        val height = gifInfo.height     // getGifHeight can be used directly
        val width = gifInfo.width

        val targetFrame = 2

        val destinationArray = IntArray(width * height)

        nsGifLib.copyPixels(destinationArray, targetFrame, gifId)

        // now you can use destinationArray, it will contain pixels of current gif frame

        /* OR if you want to have a rendered 'targetFrame', you can use below logic */

        // obtain current frame
        val currentFrame = nsGifLib.getGifCurrentFrame(gifId)
        // calculate difference
        var difference = targetFrame - currentFrame

        // if we are on the same frame
        if (difference == 0) {
            nsGifLib.copyPixels(destinationArray, targetFrame, gifId)
        } else if (difference < 0) {
            val frameCount = gifInfo.frames
            // calculate required frames till target
            difference = frameCount - targetFrame - difference
        }

        // set current frame to target
        repeat(difference) { diff ->
            nsGifLib.setGifFrame(diff, gifId)
        }

        // nsGifLib.copyPixels(destinationArray, targetFrame, gifId) - not needed, current frame is already == target
        nsGifLib.copyPixels(destinationArray, gifId)

        // use target frame
    }

    private fun getGifInfo() {
        val gifId = 0   // should be obtained from setGif method
        val gifInfo = nsGifLib.getGifInfo(gifId)
        gifInfo
        // now you can use gifInfo

        // for more information, see [NsGifInfo] class
    }
}