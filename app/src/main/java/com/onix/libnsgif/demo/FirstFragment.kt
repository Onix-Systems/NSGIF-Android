package com.onix.libnsgif.demo

import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.libnsgif.NsGifLib
import com.libnsgif.android.builder.RestoreStrategy
import com.onix.libnsgif.demo.databinding.FragmentFirstBinding

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
                .withGif(resources.assets, "tiger.gif")
                .withOffset(20)
                .withScaleType(Matrix.ScaleToFit.CENTER)
                .withRestoreStrategy(RestoreStrategy.LAST_FRAME)
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

                    if (count % 3 == 0) {
                        image.setGif(resources.assets, "waves.gif")
                    } else if (count % 2 == 0) {
                        image.setGif(resources.assets, "man2.gif")
                    } else {
                        image.setGif(resources.assets, "keyboard.gif")
                    }
                    count++
                }
            }
        }
    }

    private var count = 0
}