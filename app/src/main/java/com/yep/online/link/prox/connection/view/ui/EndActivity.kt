package com.yep.online.link.prox.connection.view.ui

import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yep.online.link.prox.connection.base.BaseActivity
import com.yep.online.link.prox.connection.base.BaseViewModel
import com.yep.online.link.prox.connection.R
import com.yep.online.link.prox.connection.base.BaseAdom
import com.yep.online.link.prox.connection.databinding.ActivityEndBinding
import com.yep.online.link.prox.connection.hlep.DataUtils
import com.yep.online.link.prox.connection.hlep.ServiceBean
import com.yep.online.link.prox.connection.net.CloakUtils
import com.yep.online.link.prox.connection.view.ad.YepLoadBackAd
import com.yep.online.link.prox.connection.view.ad.YepLoadEndAd
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class EndActivity : BaseActivity<BaseViewModel, ActivityEndBinding>() {

    override fun getLayoutRes(): Int = R.layout.activity_end

    override fun getViewModelClass(): Class<BaseViewModel> = BaseViewModel::class.java
    private var isConnectionYep: Boolean = false

    //当前服务器
    private lateinit var serverBeanYep: ServiceBean

    override fun init() {
        BaseAdom.getResultInstance().whetherToShowYep = false
        val bundle = intent.extras
        isConnectionYep = bundle?.getBoolean(DataUtils.connectionYepStatus) == true
        serverBeanYep = Gson().fromJson(
            bundle?.getString(DataUtils.serverYepInformation),
            object : TypeToken<ServiceBean?>() {}.type
        )
        binding.imageView.setOnClickListener {
            returnToHomePage()
        }
        binding.vpnState = isConnectionYep
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                returnToHomePage()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        showEndAd()
    }

    private fun returnToHomePage() {
        if (YepLoadBackAd.displayBackAdvertisementYep(this, cloneAd = {
                finish()
            }) != 2) {
            finish()
        }
        CloakUtils.putPointYep("resultsback", this@EndActivity)
    }

    private fun showEndAd() {
        lifecycleScope.launch {
            delay(200)
            if (lifecycle.currentState != Lifecycle.State.RESUMED) {
                return@launch
            }
            CloakUtils.putPointYep("resultsview", this@EndActivity)
            val adEndData = BaseAdom.getResultInstance().appAdDataYep
            if (adEndData == null) {
                BaseAdom.getResultInstance().advertisementLoadingYep(this@EndActivity)
            }
            while (isActive) {
                if (adEndData != null) {
                    YepLoadEndAd.setDisplayEndNativeAdYep(this@EndActivity)
                    cancel()
                    break
                }
                delay(500)
            }
        }
    }
}