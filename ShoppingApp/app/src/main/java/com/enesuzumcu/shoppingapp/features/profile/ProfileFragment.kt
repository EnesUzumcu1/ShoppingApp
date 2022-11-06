package com.enesuzumcu.shoppingapp.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.enesuzumcu.shoppingapp.R
import com.enesuzumcu.shoppingapp.databinding.FragmentProfileBinding
import com.enesuzumcu.shoppingapp.features.loadingprogressbar.LoadingProgressBar
import com.enesuzumcu.shoppingapp.features.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    lateinit var loadingProgressBar: LoadingProgressBar
    private val viewModel by viewModels<ProfileViewModel>()
    private var navController: androidx.navigation.NavController? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        loadingProgressBar = LoadingProgressBar(requireContext())
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiEvent.collect {
                    when (it) {
                        is ProfileViewEvent.ShowData -> {
                            binding.userInfo = it.data
                        }
                        is ProfileViewEvent.ShowError -> {
                            Snackbar.make(requireView(), it.error, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            launch {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ProfileUiState.Loading -> loadingProgressBar.show()
                        else -> loadingProgressBar.hide()
                    }
                }
            }
        }

        binding.btnSignOut.setOnClickListener {
            alertDialog()
        }


    }

    private fun alertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sign Out")
        builder.setMessage("Are you sure you want to end the session?")

        builder.setPositiveButton("Yes") { dialog, which ->
            viewModel.signout()
            navController!!.navigate(R.id.signInAndSignUpFragment)
            (requireActivity() as MainActivity).binding.isVisibleBar = false
            (requireActivity() as MainActivity).binding.toolbar.isVisibleToolBar = false
            (requireActivity() as MainActivity).binding.toolbar.totalPrice = ""
            (requireActivity() as MainActivity).binding.toolbar.visibilityBasket = false
        }

        builder.setNeutralButton("No") { dialog, which ->
            dialog.dismiss()
            viewModel.alertDialog.call()
        }
        builder.show()
        viewModel.alertDialog.value = builder.create()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingProgressBar.dismiss()
    }
}