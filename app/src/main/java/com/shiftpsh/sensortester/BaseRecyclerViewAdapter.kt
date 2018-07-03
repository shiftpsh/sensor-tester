package com.shiftpsh.sensortester

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View


abstract class BaseRecyclerViewAdapter<T, VT : BaseItemViewModel<T>> : RecyclerView.Adapter<BaseRecyclerViewAdapter.ItemViewHolder<T, VT>>() {

    var items: ArrayList<T> = ArrayList()

    override fun onBindViewHolder(holder: ItemViewHolder<T, VT>, position: Int) {
        holder.setItem(items[position])
    }

    override fun getItemCount() = items.size

    class ItemViewHolder<in T, out VT : BaseItemViewModel<T>>(itemView: View, private val binding: ViewDataBinding, private val viewModel: VT) : RecyclerView.ViewHolder(itemView) {
        internal fun setItem(item: T) {
            viewModel.setItem(item)
            binding.executePendingBindings()
        }
    }
}