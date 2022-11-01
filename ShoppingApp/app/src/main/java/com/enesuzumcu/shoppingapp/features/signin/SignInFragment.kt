package com.enesuzumcu.shoppingapp.features.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.enesuzumcu.shoppingapp.R
import com.enesuzumcu.shoppingapp.databinding.FragmentSigninBinding
import com.enesuzumcu.shoppingapp.features.loadingprogressbar.LoadingProgressBar
import com.enesuzumcu.shoppingapp.features.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment() {
    lateinit var loadingProgressBar: LoadingProgressBar
    private lateinit var binding: FragmentSigninBinding
    private val viewModel by viewModels<SignInViewModel>()
    private var navController: androidx.navigation.NavController? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninBinding.inflate(inflater, container, false)
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
                        is SignInViewEvent.NavigateToMain -> {
                            navController?.navigate(R.id.nav_graph, null)
                            (requireActivity() as MainActivity).binding.bottomNavigation.visibility =
                                View.VISIBLE

                            Snackbar.make(requireView(), "Login Success", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is SignInViewEvent.ShowError -> {
                            Snackbar.make(requireView(), it.error, Toast.LENGTH_SHORT)
                                .show()
                        }

                    }
                }
            }
            launch {
                viewModel.uiState.collect{ uiState->
                    when(uiState){
                        is SignInUiState.Loading -> {loadingProgressBar.show()}
                        else -> loadingProgressBar.hide()
                    }

                }
            }
        }

        binding.btnLogin.setOnClickListener {
            viewModel.login(
                binding.etMailLogin.text.trim().toString(),
                binding.etPasswordLogin.text.trim().toString()
            )
        }
    }
}
