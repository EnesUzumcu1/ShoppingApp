package com.enesuzumcu.shoppingapp.features.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.enesuzumcu.shoppingapp.R
import com.enesuzumcu.shoppingapp.databinding.FragmentSignupBinding
import com.enesuzumcu.shoppingapp.features.loadingprogressbar.LoadingProgressBar
import com.enesuzumcu.shoppingapp.features.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    lateinit var loadingProgressBar: LoadingProgressBar
    private lateinit var binding: FragmentSignupBinding
    private val viewModel by viewModels<SignUpViewModel>()
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        loadingProgressBar = LoadingProgressBar(requireContext())
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiEvent.collect{
                    when(it){
                        is SignUpViewEvent.NavigateToMain -> {
                            navController?.navigate(
                                R.id.nav_graph,null
                            )
                            (requireActivity() as MainActivity).binding.isVisibleBar = true
                            (requireActivity() as MainActivity).binding.toolbar.isVisibleToolBar = true
                            Snackbar.make(requireView(), "Register Success", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                        is SignUpViewEvent.ShowError ->{
                            Snackbar.make(requireView(), it.error, Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            launch {
                viewModel.uiState.collect{ uiState->
                    when(uiState){
                        is SignUpUiState.Loading -> loadingProgressBar.show()
                        else -> loadingProgressBar.hide()
                    }

                }
            }
        }
        initViews()
    }
    private fun initViews() {
        with(binding) {
            btnRegister.setOnClickListener {
                viewModel.register(
                    etMailLogin.text.trim().toString(),
                    etPasswordLogin.text.trim().toString(),
                    etConfirmPasswordLogin.text.trim().toString(),
                    etUserName.text.trim().toString()
                )
            }
        }
    }
}