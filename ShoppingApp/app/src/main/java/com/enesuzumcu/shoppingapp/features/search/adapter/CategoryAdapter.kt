package com.enesuzumcu.shoppingapp.features.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.enesuzumcu.shoppingapp.databinding.ItemRecyclerviewCategoryBinding

class CategoryAdapter (private val list: ArrayList<String>, private val listener: OnCategoryClickListener) :
    RecyclerView.Adapter<CategoryAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ItemRecyclerviewCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(list[position], listener)
    }

    class ProductViewHolder(private val binding: ItemRecyclerviewCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listItem: String, listener: OnCategoryClickListener) {
            binding.category = listItem
            binding.listener = listener
            binding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

interface OnCategoryClickListener {
    fun onCategoryClick(categoryName: String, view: View)
}