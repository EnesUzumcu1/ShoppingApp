package com.enesuzumcu.shoppingapp.features.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.enesuzumcu.shoppingapp.R
import com.enesuzumcu.shoppingapp.databinding.ActivityOnboardingBinding
import com.enesuzumcu.shoppingapp.features.main.MainActivity
import com.enesuzumcu.shoppingapp.features.onboarding.adapter.OnBoardingAdapter
import com.enesuzumcu.shoppingapp.utils.extensions.gone
import com.enesuzumcu.shoppingapp.utils.extensions.visible
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {
    private val viewModel by viewModels<OnBoardingViewModel>()
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = OnBoardingAdapter(this, prepareOnBoardingItems())
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
        }.attach()

        initViews()
    }

    private fun initViews() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.isLastPage = position == prepareOnBoardingItems().size-1
                if (position != 0) {
                    binding.btnPrev.visible()
                } else {
                    binding.btnPrev.gone()
                }
            }
        })

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem == prepareOnBoardingItems().size-1) {
                skipOnBoarding()
            } else {
                binding.viewPager.setCurrentItem(binding.viewPager.currentItem.plus(1), true)
            }
        }

        binding.btnPrev.setOnClickListener {
            binding.viewPager.setCurrentItem(binding.viewPager.currentItem.minus(1), true)
        }

        binding.btnSkip.setOnClickListener {
            skipOnBoarding()
        }
    }

    private fun skipOnBoarding() {
        viewModel.setOnBoardingStatus()
        navigateToMain()
    }

    private fun prepareOnBoardingItems(): List<Int> {
        return listOf(
            R.layout.item_onboarding_one,
            R.layout.item_onboarding_two,
            R.layout.item_onboarding_three,
            R.layout.item_onboarding_four
        )
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.KEY_NAVIGATE_HOME, true)
        startActivity(intent)
        finish()
    }
}