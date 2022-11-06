package com.enesuzumcu.shoppingapp.features.basket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.enesuzumcu.shoppingapp.data.model.ProductDTO
import com.enesuzumcu.shoppingapp.databinding.ItemRecyclerviewBasketBinding

class BasketAdapter(private val listener: OnBasketClickListener) :
    ListAdapter<ProductDTO, BasketAdapter.BasketViewHolder>(BasketDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
        return BasketViewHolder(
            ItemRecyclerviewBasketBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class BasketViewHolder(private val binding: ItemRecyclerviewBasketBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ProductDTO, listener: OnBasketClickListener) {
            binding.dataHolder = data
            binding.listener = listener
            binding.executePendingBindings()
        }

    }

    class BasketDiffUtil : DiffUtil.ItemCallback<ProductDTO>() {
        override fun areItemsTheSame(oldItem: ProductDTO, newItem: ProductDTO): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductDTO, newItem: ProductDTO): Boolean {
            return oldItem == newItem
        }

    }

}

interface OnBasketClickListener {
    fun onBasketDeleteClick(productDTO: ProductDTO)
    fun onAddBtnClick(productDTO: ProductDTO,newQuantity: String)
    fun onSubsBtnClick(productDTO: ProductDTO,newQuantity: String)
}