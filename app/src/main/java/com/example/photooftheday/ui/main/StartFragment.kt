package com.example.photooftheday.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.photooftheday.*
import com.example.photooftheday.databinding.MainFragmentMotionBinding
import com.example.photooftheday.model.pod.AppStatePOD
import com.example.photooftheday.ui.secondary.FavoriteFragment
import com.example.photooftheday.viewModel.ViewModelPOD
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.chip.Chip

class StartFragment : Fragment() {
    private var _binding: MainFragmentMotionBinding? = null
    private val binding get() = _binding!!

    private val viewModelPOD: ViewModelPOD by lazy {
        ViewModelProvider(this).get(ViewModelPOD::class.java)
    }
    private lateinit var sharedPref: SharedPref
    private lateinit var chips: List<Chip>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = MainFragmentMotionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPref = SharedPref(requireActivity())
        if (sharedPref.getSharedPref(THEME) != 1) sharedPref.setSharedPref(
            THEME, resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        )
        if (savedInstanceState == null) {
            sharedPref.setSharedPref(HD, 0)
            sharedPref.setSharedPref(DAY, 0)
        }
        binding.motion.chipsLayoutNew.let {
            chips = listOf(it.chipToday, it.chipYesterday, it.chipDayBeforeYesterday)
        }
        setOnClickListenersForChips()
        viewModelPOD.getData(sharedPref.getSharedPref(DAY))
            .observe(viewLifecycleOwner, { renderData(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SpannableString("NASA: picture if the day").apply {
            setSpan(ForegroundColorSpan(requireActivity().getColor(R.color.green)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(requireActivity().getColor(R.color.purple_200)), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(requireActivity().getColor(R.color.blue)), 3, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.motion.label.text = this
        }

        setBottomAppBar(view)
        binding.scrolling.inputLayout.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data =
                    Uri.parse("https://en.wikipedia.org/wiki/${binding.scrolling.textInputEdit.text.toString()}")
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
                    if (sharedPref.getSharedPref(HD) == 1 && data.serverResponseData.media_type == "image") serverResponseData.hdurl.toString() else {
                        serverResponseData.url.toString()
                    }

                if (url.isEmpty()) Toast.makeText(context, "ссылка пустая!", Toast.LENGTH_LONG)
                    .show() else {
                    binding.scrolling.apply {
                        if (serverResponseData.title != null) SpannableString(serverResponseData.title).apply {
                            setSpan(ForegroundColorSpan(requireActivity().getColor(R.color.blue)),
                                0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            textHeader.text = this
                        }
                        textDescription.text = serverResponseData.explanation

                        var firstSpace = 0
                        var secondSpace = 0
                        var thirdSpace = 0
                        var fourthSpace = 0
                        var space = 0

                        textHeader.text.forEachIndexed {idx, e ->
                            if (e.toString() == " ") space += 1
                            when (space) {
                                1 -> { if (firstSpace == 0) firstSpace = idx + 1 }
                                2 -> { if (secondSpace == 0) secondSpace = idx + 1 }
                                3 -> { if (thirdSpace == 0) thirdSpace = idx + 1 }
                                4 -> { if (fourthSpace == 0) fourthSpace = idx + 1 }
                            }
                        }
                        SpannableString(textHeader.text).apply {
                            setSpan(ForegroundColorSpan(requireActivity().getColor(R.color.green)), 0, firstSpace, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (secondSpace != 0) setSpan(ForegroundColorSpan(requireActivity().getColor(R.color.purple_200)), secondSpace, thirdSpace, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (thirdSpace != 0) setSpan(ForegroundColorSpan(requireActivity().getColor(R.color.blue)), thirdSpace, fourthSpace, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            textHeader.text = this
                        }

                        when (serverResponseData.media_type) {
                            "image" -> {
                                webView.visibility = View.GONE
                                webView.onPause()
                                imageView.visibility = View.VISIBLE
                                imageView.load(url) {
                                    lifecycle(this@StartFragment)
                                    error(R.drawable.ic_error)
                                }
                            }
                            "video" -> {
                                imageView.visibility = View.GONE
                                webView.visibility = View.VISIBLE
                                webView.settings.javaScriptEnabled = true
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
                binding.scrolling.apply {
                    webView.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    imageView.setImageResource(R.drawable.ic_error)
                }
            }
        }
    }

    private fun setOnClickListenersForChips() {
        for (i in chips.indices) {
            chips[i].setTypeface(getFont(requireContext(), R.font.willow_body), Typeface.NORMAL)
            chips[i].setOnClickListener {
                viewModelPOD.getData(i)
                sharedPref.setSharedPref(DAY, i)
                chipsCheck(i)
            }
        }
    }

    private fun chipsCheck(day: Int) {
        for (i in chips.indices) {
            chips[i].isChecked = i == day.also {
                chips[i].setTypeface(getFont(requireContext(), R.font.willow_body), Typeface.NORMAL)
                chips[it].setTypeface(getFont(requireContext(), R.font.willow_body), Typeface.ITALIC)
            }
        }
    }

    companion object {
        fun newInstance() = StartFragment()
        private var isMain = true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_bottom_bar, menu)
        if (sharedPref.getSharedPref(HD) == 1) menu.findItem(R.id.app_bar_hd)
            .setIcon(R.drawable.ic_hd_colored)
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
                    ActivityCompat.recreate(requireActivity())
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
            android.R.id.home -> activity?.let {
                BottomSheetDialogFragment().show(it.supportFragmentManager, "tag")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBottomAppBar(view: View) {
        val context = activity as MainActivity
        context.setSupportActionBar(view.findViewById(R.id.bottom_appBar))
        setHasOptionsMenu(true)
        if (!isMain) setSettingsForBottom(context)
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
                    if (sharedPref.getSharedPref(HD) == 1) menu.findItem(R.id.app_bar_hd)
                        .setIcon(R.drawable.ic_hd_colored)
                }
                requireActivity().supportFragmentManager.popBackStack(
                    "favorite", FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                binding.fab.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_favorite))
            }
        }
    }

    private fun setSettingsForBottom(context: MainActivity) {
        binding.bottomAppBar.navigationIcon = null
        binding.fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_back))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}