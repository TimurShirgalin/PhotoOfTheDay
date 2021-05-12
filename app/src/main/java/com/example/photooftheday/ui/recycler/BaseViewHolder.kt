package com.example.photooftheday.ui.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.photooftheday.model.recycler.DataRecycler

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(dataItem: Pair<DataRecycler, Boolean>)
}