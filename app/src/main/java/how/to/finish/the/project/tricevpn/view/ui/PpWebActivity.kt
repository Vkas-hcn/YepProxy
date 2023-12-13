package how.to.finish.the.project.tricevpn.view.ui

import com.dual.pro.one.dualprotocolone.base.BaseActivity
import com.dual.pro.one.dualprotocolone.base.BaseViewModel
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.databinding.ActivityPpWebBinding
import how.to.finish.the.project.tricevpn.hlep.DataUtils

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