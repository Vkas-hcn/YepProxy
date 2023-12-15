package how.to.finish.the.project.tricevpn.view.ui

import android.content.Intent
import android.view.KeyEvent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import how.to.finish.the.project.tricevpn.base.BaseActivity
import how.to.finish.the.project.tricevpn.base.BaseViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import how.to.finish.the.project.tricevpn.BuildConfig
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.base.BaseAdom
import how.to.finish.the.project.tricevpn.databinding.ActivityStartBinding
import how.to.finish.the.project.tricevpn.hlep.AdUtils
import how.to.finish.the.project.tricevpn.hlep.DataUtils
import how.to.finish.the.project.tricevpn.net.YepOkHttpUtils
import how.to.finish.the.project.tricevpn.view.ad.YepLoadOpenAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class StartActivity : BaseActivity<BaseViewModel, ActivityStartBinding>() {
    private var jobOpenAdsYep: Job? = null
    private var startCateYep: Job? = null
    var progressInt = 0

    override fun getLayoutRes(): Int = R.layout.activity_start

    override fun getViewModelClass(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun init() {
        lifecycleScope.launch(Dispatchers.IO) {
            YepOkHttpUtils().getCurrentIp()
            YepOkHttpUtils().getBlackList(this@StartActivity)
        }
        Thread(Runnable {
            for (i in 0..100) {
                binding.progressBarStart.progress = i
                Thread.sleep(120)
            }
        }).start()
        AdUtils.getFileBaseData(this, loadAdFun = {
            loadAdFun()
        })
    }


    private fun loadAdFun() {
        // 开屏
        BaseAdom.getOpenInstance().advertisementLoadingYep(this)
        loadOpenAd()
        // 首页原生
        BaseAdom.getHomeInstance().advertisementLoadingYep(this)
        // 结果页原生
        BaseAdom.getResultInstance().advertisementLoadingYep(this)
        // 连接插屏
        BaseAdom.getConnectInstance().advertisementLoadingYep(this)
        // 服务器页插屏
        BaseAdom.getBackInstance().advertisementLoadingYep(this)
    }

    private fun loadOpenAd() {
        jobOpenAdsYep?.cancel()
        jobOpenAdsYep = null
        jobOpenAdsYep = lifecycleScope.launch {
            try {
                withTimeout(10000L) {
                    while (isActive) {
                        val showState = YepLoadOpenAd
                            .displayOpenAdvertisementYep(this@StartActivity, fullScreenFun = {
                                startToMain()
                            })
                        if (showState) {
                            cancel()
                            jobOpenAdsYep = null
                            progressInt = 100
                            binding.progressBarStart.progress = progressInt
                        }
                        delay(500L)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                cancel()
                jobOpenAdsYep = null
                progressInt = 100
                binding.progressBarStart.progress = progressInt
                startToMain()
            }
        }
    }
    private fun startToMain() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    //倒计时2秒跳转首页
    override fun onResume() {
        super.onResume()

    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyCode == KeyEvent.KEYCODE_BACK
    }
}