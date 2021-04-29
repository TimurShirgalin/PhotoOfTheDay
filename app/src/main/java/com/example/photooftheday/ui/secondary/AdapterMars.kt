package com.example.photooftheday.ui.secondary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.photooftheday.R
import com.example.photooftheday.model.mars.DataMarsPhoto
import com.example.photooftheday.ui.main.EquilateralImageView

class AdapterMars : RecyclerView.Adapter<AdapterMars.ViewHolder>() {

    private var marsData: List<DataMarsPhoto> = listOf()

    fun setMovieData(data: List<DataMarsPhoto>) {
        marsData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_mars_photo, parent, false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(marsData[position])

    override fun getItemCount() = marsData.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var image: EquilateralImageView = itemView.findViewById(R.id.image_mars)

        fun bind(urlPhoto: DataMarsPhoto) {
            image.load("https${urlPhoto.img_src.drop(4)}")
        }
    }
}