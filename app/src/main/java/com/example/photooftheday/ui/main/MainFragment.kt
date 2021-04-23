package com.example.photooftheday.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.photooftheday.MainActivity
import com.example.photooftheday.R
import com.example.photooftheday.databinding.MainFragmentBinding
import com.example.photooftheday.viewModel.AppState
import com.example.photooftheday.viewModel.MainViewModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*

class MainFragment : Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private var imageDescriptionHeaderText: String? = null
    private var imageDescriptionText: String? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getData(0)
            .observe(viewLifecycleOwner, { renderData(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomAppBar(view)
        setOnClickListenersForChips()
        setBottomSheet(view.findViewById(R.id.bottom_sheet_container))
        binding.inputLayout.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data =
                    Uri.parse("https://en.wikipedia.org/wiki/${binding.textInputEdit.text.toString()}")
            })
        }
    }

    private fun renderData(data: AppState) {
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
                    imageDescription.alpha = 0f
                    imageDescription.text = imageDescriptionText
                    binding.imageView.load(url) {
                        lifecycle(this@MainFragment)
                        error(R.drawable.ic_error)
                    }
                }
            }
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
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
        binding.chipsLayout.chipToday.setOnClickListener {
            viewModel.getData(0)
        }
        binding.chipsLayout.chipYesterday.setOnClickListener {
            viewModel.getData(1)
        }
        binding.chipsLayout.chipDayBeforeYesterday.setOnClickListener {
            viewModel.getData(2)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_fav -> Toast.makeText(context, "Favorite", Toast.LENGTH_SHORT).show()
//            R.id.app_bar_settings -> displayMaterialSnackBar()
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
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_back
                        )
                    )
                    it.replaceMenu(R.menu.menu_search)
                }
            } else {
                isMain = true
                binding.bottomAppBar.also {
                    it.navigationIcon =
                        ContextCompat.getDrawable(context, R.drawable.ic_menu)
                    it.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                    binding.fab.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_plus
                        )
                    )
                    it.replaceMenu(R.menu.menu_bottom_bar)
                }
            }
        }
    }
}