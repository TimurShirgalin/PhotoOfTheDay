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
import androidx.lifecycle.ViewModelProvider
import com.example.photooftheday.DAY
import com.example.photooftheday.THEME
import com.example.photooftheday.R
import com.example.photooftheday.SharedPref
import com.example.photooftheday.databinding.MainFragmentBinding
import com.example.photooftheday.ui.media.ImageFragment
import com.example.photooftheday.ui.media.VideoFragment
import com.example.photooftheday.model.AppState
import com.example.photooftheday.viewModel.MainViewModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip


class MainFragment : Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private var imageDescriptionHeaderText: String? = null
    private var imageDescriptionText: String? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private lateinit var sharedPref: SharedPref
    private lateinit var chips: List<Chip>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPref = SharedPref(activity!!)
        if (sharedPref.getSharedPref(THEME) != 1) {
            sharedPref.setSharedPref(
                THEME, resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            )
        }
        if (savedInstanceState == null) {
            sharedPref.setSharedPref(DAY, 0)
        }
        chips = listOf(
            binding.chipsLayout.chipToday,
            binding.chipsLayout.chipYesterday,
            binding.chipsLayout.chipDayBeforeYesterday
        )
        setOnClickListenersForChips()
        viewModel.getData(sharedPref.getSharedPref(DAY))
            .observe(viewLifecycleOwner, { renderData(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomAppBar(view)
        setBottomSheet(view.findViewById(R.id.bottom_sheet_container))
        binding.inputLayout.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data =
                    Uri.parse("https://en.wikipedia.org/wiki/${binding.textInputEdit.text.toString()}")
            })
        }
    }

    private fun renderData(data: AppState) {
        chipsCheck(sharedPref.getSharedPref(DAY))
        when (data) {
            is AppState.Success -> {
                binding.loadingLayout.visibility = View.GONE

                val serverResponseData = data.serverResponseData
                val url = serverResponseData.url
                if (url.isNullOrEmpty()) {
                    Toast.makeText(context, "ссылка пустая!", Toast.LENGTH_LONG).show()
                } else {
                    imageDescriptionHeaderText = serverResponseData.title
                    imageDescriptionText = serverResponseData.explanation
                    val imageDescription: TextView =
                        activity!!.findViewById(R.id.bottom_sheet_description)
                    val imageDescriptionHeader: TextView =
                        activity!!.findViewById(R.id.bottom_sheet_description_header)
                    imageDescriptionHeader.text = imageDescriptionHeaderText
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        imageDescription.alpha = 0f
                    }
                    imageDescription.text = imageDescriptionText
                    when (serverResponseData.media_type) {
                        "image" -> {
                            val bundle = Bundle()
                            bundle.putParcelable(ImageFragment.KEY, data.serverResponseData)
                            activity!!.supportFragmentManager.beginTransaction()
                                .replace(R.id.media_container, ImageFragment.newInstance(bundle))
                                .commitNow()
                        }
                        "video" -> {
                            val bundle = Bundle()
                            bundle.putParcelable(VideoFragment.KEY_V, data.serverResponseData)
                            activity!!.supportFragmentManager.beginTransaction()
                                .replace(R.id.media_container, VideoFragment.newInstance(bundle))
                                .commitNow()
                        }
                    }
                }
            }
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                val bundle = Bundle()
                bundle.putString(ImageFragment.KEY, "dataError")
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.media_container, ImageFragment.newInstance(bundle))
                    .commitNow()
            }
        }
    }

    @SuppressLint("SimpleDateFormat", "ResourceType")
    private fun setBottomSheet(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        val imageDescription: TextView = activity!!.findViewById(R.id.bottom_sheet_description)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                imageDescription.animate().alpha(slideOffset).setDuration(0).start()
            }
        })
    }

    private fun setOnClickListenersForChips() {
        for (i in chips.indices) {
            chips[i].setOnClickListener {
                viewModel.getData(i)
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
    }

    @SuppressLint("CommitPrefEdits")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_fav -> Toast.makeText(context, "Favorite", Toast.LENGTH_SHORT).show()
            R.id.app_bar_settings -> {
                val list = arrayOf("Светлая тема", "Темная тема", "Черно-синяя тема")
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Тема приложения")
                    .setItems(list) { _, which ->
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
                                context!!.setTheme(R.style.Theme_Cosmic)
                                sharedPref.setSharedPref(THEME, 1)
                            }
                        }
                        recreate(activity!!)
                    }
                val dialog = builder.create()
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_shape)
                dialog.show()
            }
            android.R.id.home -> {
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
        binding.fab.setOnClickListener {
            if (isMain) {
                isMain = false
                binding.bottomAppBar.also {
                    it.navigationIcon = null
                    it.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                    binding.fab.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_back)
                    )
                    it.replaceMenu(R.menu.menu_search)
                }
            } else {
                isMain = true
                binding.bottomAppBar.also {
                    it.navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_menu)
                    it.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                    binding.fab.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_plus)
                    )
                    it.replaceMenu(R.menu.menu_bottom_bar)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}