package com.libnsgif.android.view

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import com.libnsgif.NsGifLib
import com.libnsgif.android.NsGifAndroid
import com.libnsgif.android.builder.GifOptionsBuilder
import com.libnsgif.android.builder.RestoreStrategy
import com.libnsgif.android.state.GifInstanceState
import com.libnsgif.entity.NsGifInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class NsGifView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val gifLib = NsGifAndroid.getInstance()
    private val scope = CoroutineScope(SupervisorJob())
    private var animJob: Job? = null
    private var preloadJob: Job? = null

    private var bitmap: Bitmap? = null
    private var id: Int = -1
    private var gifInfo = NsGifInfo()

    private var currentFrame = 0
    private var startOffset = 0
    private var scaleType = Matrix.ScaleToFit.CENTER
    private var restoreStrategy = RestoreStrategy.RESTART
    private var drawMatrix = Matrix()
    private val paint = Paint()
    private val bitmapRect = RectF()
    private val screenRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        getBitmap()?.let { bitmap ->
            bitmapRect.set(
                0f,
                0f,
                bitmap.width.toFloat(),
                bitmap.height.toFloat()
            )
            screenRect.set(
                0f,
                0f,
                width.toFloat(),
                height.toFloat()
            )
            drawMatrix.setRectToRect(bitmapRect, screenRect, scaleType)
            canvas.drawBitmap(bitmap, drawMatrix, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            val desiredWidth = if (widthMode == MeasureSpec.EXACTLY) {
                MeasureSpec.getSize(widthMeasureSpec)
            } else {
                calculateWidth()
            }

            val desiredHeight = if (heightMode == MeasureSpec.EXACTLY) {
                MeasureSpec.getSize(heightMeasureSpec)
            } else {
                calculateHeight()
            }

            val adjustedWidth = desiredWidth + paddingLeft + paddingRight
            val adjustedHeight = desiredHeight + paddingTop + paddingBottom

            setMeasuredDimension(adjustedWidth, adjustedHeight)
        }
    }

    private fun calculateHeight(): Int {
        return getBitmap()?.height ?: 0
    }

    private fun calculateWidth(): Int {
        return getBitmap()?.width ?: 0
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animationStop()
        resetGif()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (gifSet()) {
            animationStart()
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val savedState = GifInstanceState(superState)
        savedState.setLastFrame(currentFrame)
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is GifInstanceState) {
            super.onRestoreInstanceState(state.superState)
//            for future releases
//            if (restoreStrategy == RestoreStrategy.LAST_FRAME) {
//                startOffset = state.getLastFrame()
//                invalidate()
//            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    fun setRestoreStrategy(strategy: RestoreStrategy) {
        restoreStrategy = strategy
    }

    fun setStartOffset(offset: Int) {
        startOffset = offset
    }

    fun setScaleType(scale: Matrix.ScaleToFit) {
        scaleType = scale
        invalidate()
    }

    fun setGif(name: String): Boolean {
        resetGif()
        id = gifLib.setGif(name)
        setupAnimation()
        return gifLib.isValid(this.id)
    }

    fun setGif(asset: AssetManager, name: String): Boolean {
        resetGif()
        id = gifLib.setGif(asset, name)
        setupAnimation()
        return gifLib.isValid(this.id)
    }

    fun setGif(resource: Context, id: Int): Boolean {
        resetGif()
        this.id = gifLib.setGif(resource, id)
        setupAnimation()
        return gifLib.isValid(this.id)
    }

    fun setGif(data: ByteArray): Boolean {
        resetGif()
        id = gifLib.setGif(data)
        setupAnimation()
        return gifLib.isValid(this.id)
    }

    fun setGif(stream: ByteArrayOutputStream): Boolean {
        resetGif()
        id = gifLib.setGif(stream.toByteArray())
        setupAnimation()
        return gifLib.isValid(this.id)
    }

    fun optionsBuilder() = GifOptionsBuilder(this)

    private fun withStartOffset() {
        if (startOffset > 0) {
            preloadJob = scope.launch(Dispatchers.IO) {
                setBitmap(gifLib.getGifFrameBitmap(id = id))

                getBitmap()?.let { bitmap ->
                    repeat(startOffset) {
                        gifLib.setGifFrame(it + 1, id)
                    }
                    gifLib.getGifFrame(bitmap = bitmap, id = id)
                    currentFrame = startOffset
                }
            }
        }
    }

    private fun resetGif() {
        if (gifSet()) {
            animationStop()
            destroyGif()
            gifInfo = NsGifInfo()
            currentFrame = 0
            id = NsGifLib.INVALID_ID
        }
    }

    private fun setupAnimation() {
        if (gifSet()) {
            gifInfo = gifLib.getGifInfo(id)
            if (currentFrame != startOffset) {
                withStartOffset()
            }
            animationStart()
        }
    }

    private fun destroyGif() {
        gifLib.destroyGif(id)
    }

    private fun animationStart() {
        if (animJob != null && animJob?.isActive == true) {
            return
        }

        animJob = scope.launch(Dispatchers.Main.immediate) {
            preloadJob?.join()
            /* debounce delay between canceling and starting job */
            delay(5)
            while (animJob?.isActive == true) {
                processBitmap()

                val timeFrame: Int = gifInfo.delayMap[currentFrame] ?: -1
                if (timeFrame >= 0) {
                    delay(timeFrame.toLong())
                } else {
                    break
                }
            }
        }
    }

    private fun processBitmap() {
        val bitmap = getBitmap()
        if (bitmap == null) {
            setBitmap(gifLib.getGifFrameBitmap(id = id))
        } else {
            increaseFrame()
            gifLib.getGifFrame(bitmap, id)
        }
        postInvalidate()
    }

    private fun increaseFrame() {
        currentFrame++
        if (currentFrame >= gifInfo.frames) {
            currentFrame = 0
        }
        gifLib.setGifFrame(currentFrame, id)
    }

    private fun animationStop() {
        if (gifSet()) {
            animJob?.cancel()
            preloadJob?.cancel()
            getBitmap()?.recycle()
            setBitmap(null)
        }
    }

    private fun getBitmap(): Bitmap? {
        return bitmap
    }

    private fun setBitmap(bitmap: Bitmap?) {
        this.bitmap = bitmap

        if (bitmap != null) {
            scope.launch(Dispatchers.Main) {
                requestLayout()
            }
        }
    }

    private fun gifSet() = (id != NsGifLib.INVALID_ID)
}
