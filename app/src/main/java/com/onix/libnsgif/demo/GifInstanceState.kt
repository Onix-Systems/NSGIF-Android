package com.onix.libnsgif.demo

import android.os.Parcel
import android.os.Parcelable
import android.view.View.BaseSavedState

class GifInstanceState : BaseSavedState {
    private var lastFrame: Int = 0

    constructor(superState: Parcelable?) : super(superState)

    constructor(parcel: Parcel) : super(parcel) {
        lastFrame = parcel.readInt()
    }

    fun getLastFrame() = lastFrame

    fun setLastFrame(frame: Int) {
        lastFrame = frame
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(lastFrame)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GifInstanceState> =
            object : Parcelable.Creator<GifInstanceState> {
                override fun createFromParcel(parcel: Parcel): GifInstanceState {
                    return GifInstanceState(parcel)
                }

                override fun newArray(size: Int): Array<GifInstanceState?> {
                    return arrayOfNulls(size)
                }
            }
    }
}
