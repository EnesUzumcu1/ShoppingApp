package com.enesuzumcu.shoppingapp.features.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.enesuzumcu.shoppingapp.R
import com.enesuzumcu.shoppingapp.data.model.ProductDTO
import com.enesuzumcu.shoppingapp.databinding.FragmentBasketBinding
import com.enesuzumcu.shoppingapp.features.basket.adapter.BasketAdapter
import com.enesuzumcu.shoppingapp.features.basket.adapter.OnBasketClickListener
import com.enesuzumcu.shoppingapp.features.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BasketFragment : BottomSheetDialogFragment(), OnBasketClickListener {
    private lateinit var binding: FragmentBasketBinding
    private val viewModel by viewModels<BasketViewModel>()
    private var navController: androidx.navigation.NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBasketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiEvent.collect {
                    when (it) {
                        is BasketViewEvent.ShowData -> {
                            binding.rvBasket.adapter = BasketAdapter(this@BasketFragment).apply {
                                submitList(it.data)
                                val totalPrice = "$${viewModel.calculateTotalPrice(it.data)}"
                                binding.tvTotalPrice.text = totalPrice
                                (requireActivity() as MainActivity).binding.toolbar.totalPrice =
                                    totalPrice

                                (requireActivity() as MainActivity).binding.toolbar.visibilityBasket =
                                    it.data.size != 0

                            }
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
            launch {
                viewModel.uiState.collect {
                    when (it) {
                        is BasketUiState.Empty -> {

                        }
                        is BasketUiState.Loading -> {

                        }
                    }
                }
            }
            launch {
                viewModel.uiEventDelete.collect {
                    when (it) {
                        is BasketDeleteViewEvent.ShowData -> {
                            dialog?.window?.decorView?.let { it1 ->
                                Snackbar.make(
                                    it1,
                                    it.message,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is BasketDeleteViewEvent.ShowError -> {
                            Snackbar.make(
                                view,
                                it.error,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        val bottomSheetDialog = requireDialog() as BottomSheetDialog
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED


        binding.btnCheckOut.setOnClickListener {
            binding.rvBasket.adapter?.let {
                if (it.itemCount > 0) {
                    alertDialog()
                }
            } ?: kotlin.run {
                dialog?.window?.decorView?.let { it1 ->
                    Snackbar.make(
                        it1,
                        "Basket is Empty",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun alertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Check Out")
        builder.setMessage("Do you confirm the cart?")

        builder.setPositiveButton("Yes") { dialog, which ->
            lifecycleScope.launchWhenResumed {
                viewModel.deleteAllProductsInBasket()
                delay(1500)
                navController?.navigate(R.id.nav_graph)
                (requireActivity() as MainActivity).binding.isVisibleBar = true
                dismiss()
                dialog.dismiss()
            }

        }

        builder.setNeutralButton("No") { dialog, which ->
            dialog.dismiss()
            viewModel.alertDialog.call()
        }
        builder.show()
        viewModel.alertDialog.value = builder.create()
    }

    override fun onBasketDeleteClick(productDTO: ProductDTO) {
        viewModel.deleteProductInBasket(productDTO.id.toString())
    }

    override fun onAddBtnClick(productDTO: ProductDTO, newQuantity: String) {
        productDTO.quantity?.let {
            var newCount = it.toInt()
            newCount++
            viewModel.updateProductQuantity(productDTO.id.toString(), newCount)
        }

    }

    override fun onSubsBtnClick(productDTO: ProductDTO, newQuantity: String) {
        productDTO.quantity?.let {
            var newCount = it.toInt()
            if (newCount != 1) {
                newCount--
                viewModel.updateProductQuantity(productDTO.id.toString(), newCount)
            } else {
                dialog?.window?.decorView?.let { it1 ->
                    Snackbar.make(
                        it1,
                        "Quantity must be greater than zero",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}