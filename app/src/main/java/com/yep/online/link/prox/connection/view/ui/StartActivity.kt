package com.yep.online.link.prox.connection.view.ui

import android.content.Intent
import android.view.KeyEvent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.yep.online.link.prox.connection.base.BaseActivity
import com.yep.online.link.prox.connection.base.BaseViewModel
import com.yep.online.link.prox.connection.R
import com.yep.online.link.prox.connection.base.BaseAdom
import com.yep.online.link.prox.connection.databinding.ActivityStartBinding
import com.yep.online.link.prox.connection.hlep.AdUtils
import com.yep.online.link.prox.connection.hlep.DataUtils
import com.yep.online.link.prox.connection.net.CloakUtils
import com.yep.online.link.prox.connection.net.YepOkHttpUtils
import com.yep.online.link.prox.connection.view.ad.YepLoadOpenAd
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
    var jumpToMainLiveData = MutableLiveData<Boolean>()
    override fun getLayoutRes(): Int = R.layout.activity_start

    override fun getViewModelClass(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun init() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (!com.yep.online.link.prox.connection.base.App.vpnState) {
                YepOkHttpUtils().getTbaIp(this@StartActivity)
            }
            YepOkHttpUtils().getCurrentIp(this@StartActivity)
            YepOkHttpUtils().getBlackList(this@StartActivity)
            YepOkHttpUtils().getSessionList(this@StartActivity)
            YepOkHttpUtils().getVpnData(this@StartActivity)
        }
        Thread {
            for (i in 0..100) {
                binding.progressBarStart.progress = i
                Thread.sleep(120)
            }
        }.start()
        AdUtils.getFileBaseData(this, loadAdFun = {
            loadAdFun()
            identificationOfBuyingVolume()
        })
        jumpToMainLiveDataFUn()
    }

    private fun identificationOfBuyingVolume() {
        if (AdUtils.isItABuyingUser()) {
            CloakUtils.putPointYep("buying", this@StartActivity)
        }
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
        lifecycleScope.launch {
            delay(300)
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                jumpToMainLiveData.value = true
            }
        }
    }
    private fun jumpToMainLiveDataFUn(){
        jumpToMainLiveData.observe(this) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(300)
            if (lifecycle.currentState != Lifecycle.State.RESUMED) {
                return@launch
            }
            if (DataUtils.isStartYep) {
                CloakUtils.putPointYep("startup", this@StartActivity)
                DataUtils.isStartYep = false
            }
        }
    }

    override fun onStop() {
        super.onStop()
        DataUtils.isStartYep = true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyCode == KeyEvent.KEYCODE_BACK
    }
}