package com.example.photooftheday.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.photooftheday.HD
import com.example.photooftheday.R
import com.example.photooftheday.SharedPref
import com.example.photooftheday.databinding.FragmentBottomSheetDialogBinding
import com.example.photooftheday.ui.recycler.RecyclerFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_one -> {
                    setFabAction(context as MainActivity, RecyclerFragment.newInstance())
                }
                R.id.navigation_two -> Toast.makeText(context, "2", Toast.LENGTH_SHORT).show()
            }
            dismiss()
            true
        }
    }

    private fun setFabAction(context: MainActivity, newInstance: Fragment) {
        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottom_appBar)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        if (isMain) {
            isMain = false
            bottomAppBar.navigationIcon = null
            fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_back))
            bottomAppBar.apply {
                fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                menu.findItem(R.id.app_bar_hd).isVisible = false
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container_favorite, newInstance, "favorite")
                .addToBackStack("favorite")
                .commit()
        } else {
            isMain = true
            bottomAppBar.apply {
                navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_menu)
                fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                replaceMenu(R.menu.menu_bottom_bar)
                if (sharedPref.getSharedPref(HD) == 1) menu.findItem(R.id.app_bar_hd)
                    .setIcon(R.drawable.ic_hd_colored)
            }
            requireActivity().onBackPressed()
            fab.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_favorite
                )
            )
        }
    }
}