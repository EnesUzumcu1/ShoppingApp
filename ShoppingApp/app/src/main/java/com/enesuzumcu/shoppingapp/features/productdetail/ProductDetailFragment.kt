package com.enesuzumcu.shoppingapp.features.productdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.enesuzumcu.shoppingapp.databinding.FragmentProductDetailBinding
import com.enesuzumcu.shoppingapp.features.basket.BasketViewModel
import com.enesuzumcu.shoppingapp.features.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private val viewModel by viewModels<ProductDetailViewModel>()
    private val viewModelBasket by viewModels<BasketViewModel>()
    private var productId: String = "0"
    private var navController: androidx.navigation.NavController? = null
    private var quantity = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        arguments?.let {
            val productId = it.getString("productId")

            productId?.let { safeProductId ->
                this.productId = safeProductId
                viewModel.getDetail(this.productId)
            }
        }
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiEvent.collect {
                    when (it) {
                        is ProductDetailViewEvent.ShowData -> {
                            binding.dataHolder = it.data
                        }
                        is ProductDetailViewEvent.ShowError -> {
                            Snackbar.make(requireView(), it.error, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            launch {
                viewModel.uiEventBasket.collect {
                    when (it) {
                        is BasketViewEvent.ShowData -> {
                            it.message?.let { safeMessage ->
                                Snackbar.make(requireView(), safeMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        is BasketViewEvent.ShowError -> {
                            it.error?.let { safeMessage ->
                                Snackbar.make(requireView(), safeMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
            //update the toolbar if there is a product in the cart
            launch {
                viewModelBasket.uiEvent.collect {
                    when (it) {
                        is com.enesuzumcu.shoppingapp.features.basket.BasketViewEvent.ShowData -> {

                            val totalPrice = "$${viewModelBasket.calculateTotalPrice(it.data)}"
                            (requireActivity() as MainActivity).binding.toolbar.totalPrice =
                                totalPrice

                            (requireActivity() as MainActivity).binding.toolbar.visibilityBasket =
                                it.data.size != 0

                        }
                        is com.enesuzumcu.shoppingapp.features.basket.BasketViewEvent.ShowError -> {

                        }
                    }
                }
            }
        }

        binding.fABtnPlus.setOnClickListener {
            quantity++
            binding.tvQuantity.text = quantity.toString()
            calculateTotalPrice()

        }
        binding.fABtnMinus.setOnClickListener {
            if (quantity > 0) {
                quantity--
                calculateTotalPrice()
            }
        }

        binding.btnAddtoCart.setOnClickListener {
            val quantityStr = binding.tvQuantity.text.toString()
            var quantity = 0
            try {
                quantity = quantityStr.toInt()
            } catch (_: java.lang.NumberFormatException) {

            }
            if (quantity > 0) {
                binding.dataHolder?.let {
                    viewModel.addToBasket(it, quantity)
                    viewModelBasket.getProductsInBasket()
                }

            } else {
                Snackbar.make(
                    requireView(),
                    "Quantity must be greater than zero",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        onBackPress()
    }

    private fun calculateTotalPrice() {
        binding.tvQuantity.text = quantity.toString()
        val price = binding.dataHolder?.price
        price?.let {
            var result = it * quantity
            result = String.format("%.2f", result).toDouble()
            binding.tvTotalPrice.text = "$ $result"
        } ?: kotlin.run {
            binding.tvTotalPrice.text = "$ 0.0"
        }
    }

    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //navController?.navigate(R.id.nav_graph)

                (requireActivity() as MainActivity).binding.isVisibleBar = true

                requireActivity().supportFragmentManager.popBackStack()
                isEnabled = false


            }
        })
    }
}