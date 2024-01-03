package com.yep.online.link.prox.connection.view.ui

import com.yep.online.link.prox.connection.base.BaseActivity
import com.yep.online.link.prox.connection.base.BaseViewModel
import com.yep.online.link.prox.connection.R
import com.yep.online.link.prox.connection.databinding.ActivityPpWebBinding
import com.yep.online.link.prox.connection.hlep.DataUtils

class PpWebActivity  : BaseActivity<BaseViewModel, ActivityPpWebBinding>() {

    override fun getLayoutRes(): Int = R.layout.activity_pp_web

    override fun getViewModelClass(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun init() {
        initWebView()
    }
    private fun initWebView(){
        binding.imageView.setOnClickListener {
            finish()
        }
        binding.ppWeb.loadUrl(DataUtils.pp_url)
        binding.ppWeb.webChromeClient = object : android.webkit.WebChromeClient() {

        }
        binding.ppWeb.webViewClient = object : android.webkit.WebViewClient() {
            override fun shouldOverrideUrlLoading(view: android.webkit.WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }
        }

    }
}