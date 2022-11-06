package com.enesuzumcu.shoppingapp.features.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.enesuzumcu.shoppingapp.R
import com.enesuzumcu.shoppingapp.data.model.CategoriesResponse
import com.enesuzumcu.shoppingapp.data.model.Product
import com.enesuzumcu.shoppingapp.databinding.FragmentSearchBinding
import com.enesuzumcu.shoppingapp.features.search.adapter.OnProductClickListener
import com.enesuzumcu.shoppingapp.features.main.MainActivity
import com.enesuzumcu.shoppingapp.features.search.adapter.CategoryAdapter
import com.enesuzumcu.shoppingapp.features.search.adapter.OnCategoryClickListener
import com.enesuzumcu.shoppingapp.features.search.adapter.SearchAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(), OnProductClickListener, OnCategoryClickListener {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModels<SearchViewModel>()
    private var navController: androidx.navigation.NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        searchProduct()
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiState.collect {
                    when (it) {
                        is SearchViewState.Success -> {
                            if (it.filteredData?.isEmpty()?.not() == true) {
                                initAdapter(it.filteredData)
                            } else {
                                it.allProducts?.let { safeData -> initAdapter(safeData) }
                            }

                        }
                        is SearchViewState.Loading -> {

                        }
                    }
                }
            }

            launch {
                viewModel.uiStateCategory.collect {
                    when (it) {
                        is CategoryViewState.Success -> {
                            it.allCategories?.let { it1 -> initAdapterCategory(it1) }
                        }
                        is CategoryViewState.Loading -> {

                        }
                    }
                }
            }
        }

    }

    private fun initAdapter(data: MutableList<Product>) {
        binding.recyclerView.adapter = SearchAdapter(this@SearchFragment).apply {
            submitList(data)
        }
    }

    private fun initAdapterCategory(data: CategoriesResponse) {
        binding.recyclerViewCategory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategory.adapter = CategoryAdapter(data, this)
    }


    private fun searchProduct() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (newText.length > 2) {
                        viewModel.searchProduct(newText)
                    } else {
                        viewModel.searchProduct("")
                    }
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchProduct(it) }
                return false
            }
        })
    }

    override fun onProductDetailClick(product: Product) {
        navController?.navigate(R.id.nav_detail, Bundle().apply {
            putString("productId", product.id.toString())
            (requireActivity() as MainActivity).binding.isVisibleBar = false
        })
    }

    override fun onCategoryClick(categoryName: String, view: View) {
        if (viewModel.clickStatus) {
            viewModel.getAllProducts(categoryName)
            //viewModel.clickStatus = false
            (view as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.clicked))
        } else {
            viewModel.getAllProducts("")
            //viewModel.clickStatus = true
            (view as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }
}