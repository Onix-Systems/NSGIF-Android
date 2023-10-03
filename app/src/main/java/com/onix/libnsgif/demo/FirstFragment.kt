package com.onix.libnsgif.demo

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.libnsgif.NsGifLib
import com.onix.libnsgif.demo.databinding.FragmentFirstBinding
import java.io.InputStream

class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentFirstBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            image.optionsBuilder()
                .withOffset(10)
                .withScaleType(Matrix.ScaleToFit.CENTER)
                .build()

            start.setOnClickListener { image.setScaleType(Matrix.ScaleToFit.START) }
            end.setOnClickListener { image.setScaleType(Matrix.ScaleToFit.END) }
            fill.setOnClickListener { image.setScaleType(Matrix.ScaleToFit.FILL) }
            center.setOnClickListener { image.setScaleType(Matrix.ScaleToFit.CENTER) }
            buttonFirst.setOnClickListener {
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            }
            buttonJni.setOnClickListener {

                NsGifLib.getInstance().apply {

                    val inputStream: InputStream = requireContext().assets.open("man.gif")

                    val id = setGif(inputStream)

                    val bitmap = Bitmap.createBitmap(
                        getGifWidth(id),
                        getGifHeight(id),
                        Bitmap.Config.ARGB_8888
                    )
                    val intArray = IntArray(getGifWidth(id) * getGifHeight(id))
                    bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                    copyPixels(intArray, 15, id)

                    bitmap.setPixels(
                        intArray,
                        0,
                        getGifWidth(id),
                        0,
                        0,
                        getGifWidth(id),
                        getGifHeight(id)
                    )
                    imv.setImageBitmap(bitmap)

                    Log.d("log", "loaded gif width: ${getGifWidth(id)}")
                    Log.d("log", "loaded gif height: ${getGifHeight(id)}")
                }
            }
        }
    }
}