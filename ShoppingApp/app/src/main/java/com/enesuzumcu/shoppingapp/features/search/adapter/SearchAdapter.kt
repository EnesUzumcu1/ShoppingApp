package com.enesuzumcu.shoppingapp.features.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.enesuzumcu.shoppingapp.data.model.Product
import com.enesuzumcu.shoppingapp.databinding.ItemRecyclerviewSearchBinding

class SearchAdapter(private val listener: OnProductClickListener) :
    ListAdapter<Product, SearchAdapter.ProductViewHolder>(ProductDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ItemRecyclerviewSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class ProductViewHolder(private val binding: ItemRecyclerviewSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Product, listener: OnProductClickListener) {
            binding.dataHolder = data
            binding.listener = listener
            binding.executePendingBindings()
        }
    }

    class ProductDiffUtil : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}

interface OnProductClickListener {
    fun onProductDetailClick(product: Product)
}