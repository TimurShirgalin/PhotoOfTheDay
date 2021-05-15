package com.example.photooftheday.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.photooftheday.*
import com.example.photooftheday.databinding.MainFragmentBinding
import com.example.photooftheday.model.pod.AppStatePOD
import com.example.photooftheday.ui.secondary.FavoriteFragment
import com.example.photooftheday.viewModel.ViewModelPOD
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip


class MainFragment : Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val viewModelPOD: ViewModelPOD by lazy {
        ViewModelProvider(this).get(ViewModelPOD::class.java)
    }
    private lateinit var sharedPref: SharedPref
    private lateinit var chips: List<Chip>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPref = SharedPref(requireActivity())
        if (sharedPref.getSharedPref(THEME) != 1) {
            sharedPref.setSharedPref(
                THEME, resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            )
        }
        if (savedInstanceState == null) {
            sharedPref.setSharedPref(HD, 0)
            sharedPref.setSharedPref(DAY, 0)
        }
        binding.chipsLayout.let {
            chips = listOf(it.chipToday, it.chipYesterday, it.chipDayBeforeYesterday)
        }
        setOnClickListenersForChips()
        viewModelPOD.getData(sharedPref.getSharedPref(DAY))
            .observe(viewLifecycleOwner, { renderData(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheet(view.findViewById(R.id.bottom_sheet_container))
        setBottomAppBar(view)
        binding.inputLayout.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data =
                    Uri.parse("https://en.wikipedia.org/wiki/${binding.textInputEdit.text.toString()}")
            })
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun renderData(data: AppStatePOD) {
        chipsCheck(sharedPref.getSharedPref(DAY))
        when (data) {
            is AppStatePOD.Success -> {
                binding.loadingLayout.visibility = View.GONE

                val serverResponseData = data.serverResponseData
                val url: String =
                    if (sharedPref.getSharedPref(HD) == 1 && data.serverResponseData.media_type == "image") {
                        serverResponseData.hdurl.toString()
                    } else {
                        serverResponseData.url.toString()
                    }
                if (url.isEmpty()) {
                    Toast.makeText(context, "ссылка пустая!", Toast.LENGTH_LONG).show()
                } else {
                    val imageDescription: TextView =
                        requireActivity().findViewById(R.id.bottom_sheet_description)
                    val imageDescriptionHeader: TextView =
                        requireActivity().findViewById(R.id.bottom_sheet_description_header)
                    imageDescriptionHeader.text = serverResponseData.title
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        imageDescription.alpha = 0f
                    }
                    imageDescription.text = serverResponseData.explanation
                    when (serverResponseData.media_type) {
                        "image" -> {
                            binding.mediaLayout.apply {
                                webView.visibility = View.GONE
                                webView.onPause()
                                imageView.visibility = View.VISIBLE
                                imageView.load(url) {
                                    lifecycle(this@MainFragment)
                                    error(R.drawable.ic_error)
                                }
                            }
                        }
                        "video" -> {
                            binding.mediaLayout.imageView.visibility = View.GONE
                            binding.mediaLayout.webView.apply {
                                visibility = View.VISIBLE
                                settings.javaScriptEnabled = true
                                loadUrl(url.toString())
                                onResume()
                            }
                        }
                    }
                }
            }
            is AppStatePOD.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppStatePOD.Error -> {
                binding.loadingLayout.visibility = View.GONE
                binding.mediaLayout.apply {
                    webView.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    imageView.setImageResource(R.drawable.ic_error)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat", "ResourceType")
    private fun setBottomSheet(bottomSheet: ConstraintLayout) {
        val imageDescription: TextView =
            requireActivity().findViewById(R.id.bottom_sheet_description)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    imageDescription.animate().alpha(slideOffset).setDuration(0).start()
                }
            })
        }
    }

    private fun setOnClickListenersForChips() {
        for (i in chips.indices) {
            chips[i].setOnClickListener {
                viewModelPOD.getData(i)
                sharedPref.setSharedPref(DAY, i)
                chipsCheck(i)
            }
        }
    }

    private fun chipsCheck(day: Int) {
        for (i in chips.indices) {
            chips[i].isChecked = i == day
        }
    }

    companion object {
        fun newInstance() = MainFragment()
        private var isMain = true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_bottom_bar, menu)
        if (sharedPref.getSharedPref(HD) == 1) {
            menu.findItem(R.id.app_bar_hd).setIcon(R.drawable.ic_hd_colored)
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_settings -> {
                val list = arrayOf("Светлая тема", "Темная тема", "Черно-синяя тема")
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Тема приложения").setItems(list) { _, which ->
                    when (which) {
                        0 -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            sharedPref.setSharedPref(THEME, 16)
                        }
                        1 -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            sharedPref.setSharedPref(THEME, 32)
                        }
                        2 -> {
                            requireContext().setTheme(R.style.Theme_Cosmic)
                            sharedPref.setSharedPref(THEME, 1)
                        }
                    }
                    recreate(requireActivity())
                }
                builder.create().apply {
                    window?.setBackgroundDrawableResource(R.drawable.dialog_shape)
                    show()
                }
            }
            R.id.app_bar_hd -> {
                if (sharedPref.getSharedPref(HD) == 0) {
                    item.setIcon(R.drawable.ic_hd_colored)
                    sharedPref.setSharedPref(HD, 1)
                } else {
                    item.setIcon(R.drawable.ic_hd)
                    sharedPref.setSharedPref(HD, 0)
                }
                viewModelPOD.getData(sharedPref.getSharedPref(DAY))
            }
            R.id.home -> {
                activity?.let {
                    BottomSheetDialogFragment().show(it.supportFragmentManager, "tag")
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBottomAppBar(view: View) {
        val context = activity as MainActivity
        context.setSupportActionBar(view.findViewById(R.id.bottom_appBar))
        setHasOptionsMenu(true)
        if (!isMain) {
            setSettingsForBottom(context)
        }
        binding.fab.setOnClickListener {
            if (isMain) {
                isMain = false
                setSettingsForBottom(context)
                binding.bottomAppBar.apply {
                    fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                    menu.findItem(R.id.app_bar_hd).isVisible = false
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container_favorite, FavoriteFragment.newInstance(), "favorite")
                    .addToBackStack("favorite")
                    .commit()
            } else {
                isMain = true
                binding.bottomAppBar.apply {
                    navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_menu)
                    fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                    replaceMenu(R.menu.menu_bottom_bar)
                    if (sharedPref.getSharedPref(HD) == 1) {
                        menu.findItem(R.id.app_bar_hd).setIcon(R.drawable.ic_hd_colored)
                    }
                }
                requireActivity().supportFragmentManager.popBackStack(
                    "favorite", FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                binding.fab.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_favorite)
                )
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                bottomSheetBehavior.isHideable = false
            }
        }
    }

    private fun setSettingsForBottom(context: MainActivity) {
        binding.bottomAppBar.navigationIcon = null
        binding.fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_back))
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}