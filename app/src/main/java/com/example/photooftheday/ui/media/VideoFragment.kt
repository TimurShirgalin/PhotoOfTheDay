package com.example.photooftheday.ui.media

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.photooftheday.databinding.VideoFragmentBinding
import com.example.photooftheday.model.DataPOD


class VideoFragment : Fragment() {
    private var _binding: VideoFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = VideoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = arguments?.getParcelable<DataPOD>(KEY_V)?.url
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(url.toString())
    }

    companion object {
        const val KEY_V = "video"

        fun newInstance(bundle: Bundle): VideoFragment {
            val fragment = VideoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}