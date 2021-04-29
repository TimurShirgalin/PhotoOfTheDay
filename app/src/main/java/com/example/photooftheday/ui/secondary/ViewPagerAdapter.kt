package com.example.photooftheday.ui.secondary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photooftheday.R
import com.example.photooftheday.model.mars.MarsPhoto


class ViewPagerAdapter(private val marsData: MutableList<MarsPhoto>) :
    RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder =
        PagerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_favorite_images, parent, false)
        )

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val marsDataRecycler: RecyclerView = holder.itemView.findViewById(R.id.recycler_mars)
        val marsDataLayoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.VERTICAL, false)
        val marsAdapter = AdapterMars()

        marsDataRecycler.layoutManager = marsDataLayoutManager
        marsDataRecycler.adapter = marsAdapter
        marsAdapter.setMovieData(marsData[position].urls)
    }

    override fun getItemCount() = marsData.size

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
