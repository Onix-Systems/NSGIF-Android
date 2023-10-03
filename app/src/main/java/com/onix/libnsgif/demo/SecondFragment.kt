package com.onix.libnsgif.demo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.onix.libnsgif.demo.databinding.FragmentSecondBinding
import com.onix.libnsgif.demo.databinding.ItemListBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class SecondFragment : Fragment() {

    private lateinit var binding: FragmentSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSecondBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().popBackStack()
        }
        setupList()
    }

    private fun setupList() {
        val adapter = Adapter()
        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        val list = mutableListOf<DHolder>()
        repeat(40) { index ->
            list.add(DHolder(index))
        }
        adapter.submitList(list)
    }

    inner class Adapter : ListAdapter<DHolder, Adapter.VHolder>(DCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
            return VHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list, parent, false)
            )
        }

        override fun onBindViewHolder(holder: VHolder, position: Int) {
            holder.bind(currentList[position])
        }

        inner class VHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val binding = ItemListBinding.bind(view)
            private val gepard = copyAssetFileToInternalStorage(
                requireContext(),
                "tiger.gif",
                requireContext().filesDir
            )
            private val jan = copyAssetFileToInternalStorage(
                requireContext(),
                "man.gif",
                requireContext().filesDir
            )

            fun bind(data: DHolder) {
                binding.itemGif.setGif(gepard?.path!!)
                binding.itemGif.setStartOffset(data.frameOffset)

                binding.itemGifWrap.setGif(jan?.path!!)
                binding.itemGifWrap.setStartOffset(data.frameOffset)
            }
        }
    }

    data class DHolder(val frameOffset: Int)
    class DCallback : DiffUtil.ItemCallback<DHolder>() {
        override fun areItemsTheSame(oldItem: DHolder, newItem: DHolder): Boolean {
            return oldItem.frameOffset == newItem.frameOffset
        }

        override fun areContentsTheSame(
            oldItem: DHolder,
            newItem: DHolder
        ): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }
    }

    fun copyAssetFileToInternalStorage(
        context: Context,
        assetFileName: String,
        destinationDir: File
    ): File? {
        try {
            val inputStream: InputStream = context.assets.open(assetFileName)
            val outputFile = File(destinationDir, assetFileName)
            val outputStream = FileOutputStream(outputFile)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            return outputFile
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}
