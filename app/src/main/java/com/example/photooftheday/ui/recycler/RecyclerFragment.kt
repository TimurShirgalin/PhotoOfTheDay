package com.example.photooftheday.ui.recycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photooftheday.R
import com.example.photooftheday.model.recycler.DataRecycler
import com.example.photooftheday.viewModel.ViewModelRecycler

class RecyclerFragment : Fragment() {

    private lateinit var itemTouchHelper: ItemTouchHelper
    private val viewModelRecycler: ViewModelRecycler by lazy {
        ViewModelProvider(this).get(ViewModelRecycler::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelRecycler.getData().observe(viewLifecycleOwner, { renderData(it) })
    }

    private fun renderData(dataRecycler: List<DataRecycler>) {
        val podDataRecycler: RecyclerView =
            requireActivity().findViewById(R.id.container_recycler_pod)
        podDataRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        podDataRecycler.adapter = AdapterRecycler(
            object : AdapterRecycler.OnListItemClickListener {
                override fun onItemClick(data: String) {
                    Toast.makeText(context, "text text text", Toast.LENGTH_SHORT).show()
                }
            }
        ).also {
            it.setRecyclerData(dataRecycler)
            itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(it))
            itemTouchHelper.attachToRecyclerView(podDataRecycler)
        }
    }

    companion object {
        fun newInstance() = RecyclerFragment()
    }
}