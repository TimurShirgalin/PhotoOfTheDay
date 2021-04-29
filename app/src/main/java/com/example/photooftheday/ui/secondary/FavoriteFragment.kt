package com.example.photooftheday.ui.secondary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.photooftheday.databinding.FragmentFavoriteBinding
import com.example.photooftheday.model.mars.AppStateMars
import com.example.photooftheday.viewModel.ViewModelMars
import com.google.android.material.tabs.TabLayoutMediator

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModelMars: ViewModelMars by lazy {
        ViewModelProvider(this).get(ViewModelMars::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelMars.getDataMars().observe(viewLifecycleOwner, { renderMarsData(it) })
    }

    private fun renderMarsData(dataMars: AppStateMars?) {
        when (dataMars) {
            is AppStateMars.Success -> {
                binding.loadingLayoutAPI.visibility = View.GONE

                val serverResponseDataMars = dataMars.serverResponseDataMars
                if (serverResponseDataMars.isNullOrEmpty()) {
                    Toast.makeText(context, "ссылка пустая!", Toast.LENGTH_LONG).show()
                } else {
                    binding.viewPager.adapter = ViewPagerAdapter(serverResponseDataMars)
                    binding.viewPager.setPageTransformer(AntiClockSpinTransformation())
                    val tabTitles = listOf("Panoramic Camera", "Front Camera")
                    TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                        tab.text = tabTitles[position]
                        binding.viewPager.setCurrentItem(tab.position, true)
                    }.attach()
                }
            }
            is AppStateMars.Loading -> {
                binding.loadingLayoutAPI.visibility = View.VISIBLE
            }
            is AppStateMars.Error -> {
                binding.loadingLayoutAPI.visibility = View.GONE
                Toast.makeText(context, "Нечего показать!", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        fun newInstance() = FavoriteFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}