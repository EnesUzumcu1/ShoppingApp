package com.enesuzumcu.shoppingapp.features.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.enesuzumcu.shoppingapp.features.onboarding.fragment.OnBoardingFragment

class OnBoardingAdapter(
    fragmentActivity: FragmentActivity,
    private val layots:List<Int>
) : FragmentStateAdapter(fragmentActivity){
    override fun getItemCount(): Int {
        return layots.size
    }

    override fun createFragment(position: Int): Fragment {
        return OnBoardingFragment.newInstance(layots[position])
    }

}