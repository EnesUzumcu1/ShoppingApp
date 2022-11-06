package com.enesuzumcu.shoppingapp.features.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.enesuzumcu.shoppingapp.R
import com.enesuzumcu.shoppingapp.databinding.ActivityMainBinding
import com.enesuzumcu.shoppingapp.features.basket.BasketFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    companion object {
        const val KEY_NAVIGATE_HOME = "KEY_NAVIGATE_HOME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiState.collect {
                    when (it) {
                        is MainUiState.Success -> {
                            initNavController(it.isNavigateHome)
                        }
                    }
                }
            }
        }

        binding.toolbar.cvIcon.setOnClickListener{
            BasketFragment().show(supportFragmentManager,"Basket")
        }
    }

    private fun initNavController(isNavigateToHome: Boolean) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        if (isNavigateToHome.not()) {
            navController.navigate(R.id.signInAndSignUpFragment)
        }
        binding.isVisibleBar = isNavigateToHome
        binding.toolbar.isVisibleToolBar = isNavigateToHome

        onBackPress(navController)
    }

    private fun onBackPress(navController: NavController){
        onBackPressedDispatcher.addCallback( object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                navController.navigate(R.id.nav_graph)
            }
        })
    }
}