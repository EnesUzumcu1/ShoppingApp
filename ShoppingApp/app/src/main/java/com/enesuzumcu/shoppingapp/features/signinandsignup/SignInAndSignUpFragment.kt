package com.enesuzumcu.shoppingapp.features.signinandsignup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.enesuzumcu.shoppingapp.R
import com.enesuzumcu.shoppingapp.databinding.FragmentSigninAndSignupBinding
import com.enesuzumcu.shoppingapp.features.signinandsignup.adapter.SignInAndSignUpAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInAndSignUpFragment : Fragment() {
    private lateinit var binding: FragmentSigninAndSignupBinding
    private var navController: androidx.navigation.NavController? = null
    private var layout: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninAndSignupBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        arguments?.let {
            layout = it.getInt("layout")
        }

        binding.viewPager.adapter = SignInAndSignUpAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            if (position ==0){
                tab.text = resources.getText(R.string.sign_in)
            } else if (position == 1){
                tab.text = resources.getText(R.string.sign_up)
            }
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    binding.tvTitle.text = resources.getText(R.string.sign_in)
                } else if(position ==1) {
                    binding.tvTitle.text = resources.getText(R.string.sign_up)
                }
            }
        })
        requireActivity().onBackPressedDispatcher.addCallback( object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance(layout: Int) =
            SignInAndSignUpFragment().apply {
                arguments = Bundle().apply {
                    putInt("layout", layout)
                }
            }
    }
}