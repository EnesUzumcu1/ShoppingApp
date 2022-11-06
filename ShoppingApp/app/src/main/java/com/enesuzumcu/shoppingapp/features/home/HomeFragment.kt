package com.enesuzumcu.shoppingapp.features.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.enesuzumcu.shoppingapp.R
import com.enesuzumcu.shoppingapp.data.model.Product
import com.enesuzumcu.shoppingapp.databinding.FragmentHomeBinding
import com.enesuzumcu.shoppingapp.features.basket.BasketViewEvent
import com.enesuzumcu.shoppingapp.features.basket.BasketViewModel
import com.enesuzumcu.shoppingapp.features.home.adapter.HomeAdapter
import com.enesuzumcu.shoppingapp.features.home.adapter.OnProductClickListener
import com.enesuzumcu.shoppingapp.features.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), OnProductClickListener {
    private val viewModel by viewModels<HomeViewModel>()
    private val viewModelBasket by viewModels<BasketViewModel>()
    private lateinit var binding: FragmentHomeBinding
    private var navController: androidx.navigation.NavController? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiState.collect {
                    when (it) {
                        is HomeViewState.Success -> {
                            HomeAdapter(this@HomeFragment)
                            binding.rvHome.adapter = HomeAdapter(this@HomeFragment).apply {
                                submitList(it.allProducts)
                            }
                        }
                        is HomeViewState.Loading -> {

                        }
                    }
                }
            }
            launch {
                viewModel.uiEvent.collect {
                    when (it) {
                        is HomeViewEvent.ShowError -> {
                            Snackbar.make(
                                binding.root,
                                it.message.toString(),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            //update the toolbar if there is a product in the cart
            launch {
                viewModelBasket.uiEvent.collect {
                    when (it) {
                        is BasketViewEvent.ShowData -> {


                            val totalPrice = "$${viewModelBasket.calculateTotalPrice(it.data)}"
                            (requireActivity() as MainActivity).binding.toolbar.totalPrice =
                                totalPrice

                            (requireActivity() as MainActivity).binding.toolbar.visibilityBasket =
                                it.data.size != 0

                        }
                        is BasketViewEvent.ShowError -> {
                            Snackbar.make(
                                requireView(),
                                it.error,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onProductDetailClick(product: Product) {
        navController?.navigate(R.id.nav_detail, Bundle().apply {
            putString("productId", product.id.toString())
            (requireActivity() as MainActivity).binding.isVisibleBar = false
        })
    }

    override fun onResume() {
        super.onResume()
        //If total Price text is null, the default value is set
        val textControl =  (requireActivity() as MainActivity).binding.toolbar.totalPrice
        if(textControl.isNullOrBlank()) (requireActivity() as MainActivity).binding.toolbar.totalPrice = "$0.0"
    }
}