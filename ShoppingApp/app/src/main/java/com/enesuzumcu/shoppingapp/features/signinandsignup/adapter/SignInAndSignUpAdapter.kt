package com.enesuzumcu.shoppingapp.features.signinandsignup.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.enesuzumcu.shoppingapp.features.signinandsignup.SignInAndSignUpFragment
import com.enesuzumcu.shoppingapp.features.signin.SignInFragment
import com.enesuzumcu.shoppingapp.features.signup.SignUpFragment

class SignInAndSignUpAdapter(
    fragment: SignInAndSignUpFragment,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> SignUpFragment()
            else -> SignInFragment()
        }
    }

}