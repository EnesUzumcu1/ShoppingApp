package com.enesuzumcu.shoppingapp.features.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.enesuzumcu.shoppingapp.databinding.ActivitySplashBinding
import com.enesuzumcu.shoppingapp.features.main.MainActivity
import com.enesuzumcu.shoppingapp.features.onboarding.OnBoardingActivity
import com.enesuzumcu.shoppingapp.features.signinandsignup.SignInAndSignUpFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel by viewModels<SplashViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiEvent.collect {
                    when (it) {
                        is SplashViewEvent.NavigateToOnBoarding -> {
                            navigateToOnBoarding()
                        }
                        is SplashViewEvent.NavigateToHome -> {
                            navigateToHome(it.isNavigateHome)
                        }
                    }
                }
            }
        }

    }

    private fun navigateToHome(isNavigateHome: Boolean) {
        lifecycleScope.launch {
            delay(2000)
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            intent.putExtra(MainActivity.KEY_NAVIGATE_HOME, isNavigateHome)
            startActivity(intent)
            finish()
        }
    }

    private fun navigateToOnBoarding() {
        lifecycleScope.launch {
            delay(2000)
            val intent = Intent(this@SplashActivity, OnBoardingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}