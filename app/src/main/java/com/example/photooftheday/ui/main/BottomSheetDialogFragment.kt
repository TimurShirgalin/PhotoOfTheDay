package com.example.photooftheday.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.photooftheday.R
import com.example.photooftheday.databinding.FragmentBottomSheetDialogBinding
import com.example.photooftheday.ui.recycler.RecyclerFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

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
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.container_favorite,
                            RecyclerFragment.newInstance(), "recycler")
                        .addToBackStack("recycler")
                        .commit()
                }
                R.id.navigation_two -> Toast.makeText(context, "2", Toast.LENGTH_SHORT).show()
            }
            dismiss()
            true
        }
    }
}