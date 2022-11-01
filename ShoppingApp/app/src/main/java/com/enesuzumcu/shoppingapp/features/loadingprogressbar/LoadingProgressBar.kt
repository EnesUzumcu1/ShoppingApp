package com.enesuzumcu.shoppingapp.features.loadingprogressbar

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.enesuzumcu.shoppingapp.R

class LoadingProgressBar(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_loading_progress)
        window?.setBackgroundDrawableResource(R.color.transparent)
        setCancelable(false)
    }
}