package com.example.photooftheday.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.example.photooftheday.R
import com.example.photooftheday.databinding.ImageFragmentBinding
import com.example.photooftheday.model.DataPOD


class ImageFragment : Fragment() {
    private var _binding: ImageFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ImageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments?.getString(KEY).equals("dataError")) {
            binding.imageView.setImageResource(R.drawable.ic_error)
        } else {
            val url = arguments?.getParcelable<DataPOD>(KEY)?.url
            binding.imageView.load(url) {
                lifecycle(this@ImageFragment)
                error(R.drawable.ic_error)
            }
        }
    }

    companion object {
        const val KEY = "image"

        fun newInstance(bundle: Bundle): ImageFragment {
            val fragment = ImageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}