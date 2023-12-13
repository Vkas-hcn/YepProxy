package how.to.finish.the.project.tricevpn.mainnnnnnn

import android.os.Bundle
import android.webkit.WebViewClient
import how.to.finish.the.project.tricevpn.baseeeeee.AppConstant
import how.to.finish.the.project.tricevpn.baseeeeee.BaseAc
import how.to.finish.the.project.tricevpn.databinding.TriceWebLayputBinding

class TriceWebActivity : BaseAc<TriceWebLayputBinding>() {
    override val binding: TriceWebLayputBinding
            by lazy { TriceWebLayputBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.triceBack.setOnClickListener { finish() }
        binding.triceWebView.canGoBack()
        binding.triceWebView.canGoForward()
        binding.triceWebView.webViewClient = object : WebViewClient() {}
        binding.triceWebView.loadUrl(AppConstant.web_page)
    }
}