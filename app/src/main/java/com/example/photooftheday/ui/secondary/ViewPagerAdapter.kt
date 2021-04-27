package com.example.photooftheday.ui.secondary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.photooftheday.R

class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {

    private val images =
        longArrayOf(getItemId(R.drawable.movie_pic1), getItemId(R.drawable.movie_pic2))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder =
        PagerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_earth, parent, false)
        )

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(images[position].toInt())
    }

    override fun getItemCount() = images.size

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var image: ImageView = itemView.findViewById(R.id.image_pic)

        fun bind(position: Int) {
            image.setImageResource(position)
        }
    }
}
