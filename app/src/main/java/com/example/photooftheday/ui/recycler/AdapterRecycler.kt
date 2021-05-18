package com.example.photooftheday.ui.recycler

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.photooftheday.R
import com.example.photooftheday.SharedPref
import com.example.photooftheday.THEME
import com.example.photooftheday.model.recycler.DataRecycler


class AdapterRecycler(private val onListItemClickListener: OnListItemClickListener) :
    RecyclerView.Adapter<BaseViewHolder>(), ItemTouchHelperAdapter {

    private lateinit var sharedPref: SharedPref
    private var recyclerData: MutableList<Pair<DataRecycler, Boolean>> = mutableListOf()

    fun setRecyclerData(data: List<DataRecycler>) {
        data.forEach { recyclerData.add(Pair(it, false)) }
        notifyDataSetChanged()
    }

    fun getRecyclerData(position: Int): Pair<DataRecycler, Boolean> {
        return recyclerData[position]
    }

    interface OnListItemClickListener {
        fun onItemClick(data: String)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        recyclerData.removeAt(fromPosition).apply {
            recyclerData.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun setLike( position: Int) {
        recyclerData[position].let {
            recyclerData[position] = Pair(DataRecycler(
                it.first.id,
                it.first.titlePOD,
                it.first.descriptionPOD,
                !it.first.like
            ), it.second
            )
            notifyItemChanged(position, Pair(DataRecycler(it.first.id, it.first.titlePOD, it.first.descriptionPOD, true), false))
        }
    }

    override fun onItemDismiss(position: Int) {
        recyclerData.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        sharedPref = SharedPref(parent.context)
        return when (viewType) {
            TYPE_PHOTO_OF_THE_DAY ->
                PODViewHolder(
                    inflater.inflate(R.layout.fragment_recycler_pod_item, parent, false) as View
                )
            else -> HeaderViewHolder(
                inflater.inflate(R.layout.fragment_recycler_header, parent, false) as View
            )
        }
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads)
        else {
//            val combinedChange =
//                createCombinedPayload(payloads as List<Change<Pair<DataRecycler, Boolean>>>)
//            val oldData = combinedChange.oldData
//            val newData = combinedChange.newData


//            if (newData.first.titlePOD != oldData.first.titlePOD) {
//                holder.itemView.findViewById<AppCompatTextView>(R.id.recycler_textView_item).text = newData.first.titlePOD
//            }

//            if (newData.second != oldData.second) {
//                holder.itemView.findViewById<AppCompatTextView>(R.id.recycler_textView_description).visibility =
//                    if (recyclerData[position].second) View.VISIBLE else View.GONE
//            }

            if (payloads.any { it is Pair<*, *> }) {
                holder.itemView.findViewById<AppCompatTextView>(R.id.recycler_textView_description).visibility =
                    if (recyclerData[position].second) View.VISIBLE else View.GONE
                holder.itemView.findViewById<AppCompatImageView>(R.id.like_image).apply {
                    if (recyclerData[position].first.like) setImageResource(R.drawable.ic_like_true) else setImageResource(R.drawable.ic_like_false)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(recyclerData[position])
    }

    inner class PODViewHolder(view: View) : BaseViewHolder(view), ItemTouchHelperViewHolder {
        override fun bind(dataItem: Pair<DataRecycler, Boolean>) {
//            itemView.setOnClickListener { onListItemClickListener.onItemClick(dataItem.first.titlePOD) }
            itemView.findViewById<AppCompatTextView>(R.id.recycler_textView_description).apply {
//                visibility = if (dataItem.second) View.VISIBLE else View.GONE
                text = dataItem.first.descriptionPOD
            }
            itemView.findViewById<AppCompatTextView>(R.id.recycler_textView_item).apply {
                setOnClickListener { toggleText() }
                text = dataItem.first.titlePOD
            }
        }

        private fun toggleText() {
            recyclerData[layoutPosition] = recyclerData[layoutPosition].let {
                it.first to !it.second
            }
            notifyItemChanged(layoutPosition, Pair(DataRecycler(0, "", "", false), true))
        }

        override fun onItemSelected() {
            if (sharedPref.getSharedPref(THEME) != 16) itemView.setBackgroundColor(Color.DKGRAY)
            else itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }
    }

    inner class HeaderViewHolder(view: View) : BaseViewHolder(view) {
        override fun bind(dataItem: Pair<DataRecycler, Boolean>) {
            itemView.findViewById<AppCompatTextView>(R.id.recycler_textView_header).apply {
                text = dataItem.first.titlePOD
                typeface = Typeface.DEFAULT_BOLD
            }
        }
    }

    inner class DiffUtilCallback(
        private var oldItems: List<Pair<DataRecycler, Boolean>>,
        private var newItems: List<Pair<DataRecycler, Boolean>>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].first.id == newItems[newItemPosition].first.id


        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].first.titlePOD == newItems[newItemPosition].first.titlePOD
    }

    fun updateList(newItems: MutableList<Pair<DataRecycler, Boolean>>) {

        val diffCallback = DiffUtilCallback(this.recyclerData, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)

        this.recyclerData = newItems
    }

    fun setItems(newItems: List<Pair<DataRecycler, Boolean>>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(recyclerData, newItems))
        result.dispatchUpdatesTo(this)
        recyclerData.clear()
        recyclerData.addAll(newItems)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADER
            else -> TYPE_PHOTO_OF_THE_DAY
        }
    }

    override fun getItemCount() = recyclerData.size

    companion object {
        private const val TYPE_PHOTO_OF_THE_DAY = 1
        private const val TYPE_HEADER = 0
    }
}