package com.yep.online.link.prox.connection.view.ad

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.yep.online.link.prox.connection.base.BaseAdom
import com.yep.online.link.prox.connection.hlep.AdUtils
import com.yep.online.link.prox.connection.hlep.DataUtils.TAG
import com.yep.online.link.prox.connection.hlep.YepAdBean
import com.yep.online.link.prox.connection.net.CloakUtils
import com.yep.online.link.prox.connection.net.YepOkHttpUtils
import com.yep.online.link.prox.connection.view.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
object YepLoadConnectAd {
    private val adBase = BaseAdom.getConnectInstance()
    fun loadConnectAdvertisementYep(context: Context, adData: YepAdBean) {
        val adRequest = AdRequest.Builder().build()
        adBase.yepAdBean = CloakUtils.beforeLoadLink(adData)
        InterstitialAd.load(
            context,
            adData.connect,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adBase.isLoadingYep = false
                    adBase.appAdDataYep = null
                    Log.d(TAG, "connect-加载失败 ")
                    CloakUtils.putPointTimeYep(
                        "adloaddissucc",
                        "connect",
                        "yn",
                        context
                    )
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    adBase.loadTimeYep = Date().time
                    adBase.isLoadingYep = false
                    adBase.appAdDataYep = interstitialAd
                    Log.d(TAG, "connect-加载成功 ")
                    interstitialAd.setOnPaidEventListener { adValue ->
                        YepOkHttpUtils().getAdList(context, adValue, interstitialAd.responseInfo, "connect", adBase.yepAdBean)
                    }
                    CloakUtils.putPointTimeYep(
                        "adloadsucc",
                        "connect",
                        "yn",
                        context
                    )
                }
            })
    }


    private fun connectScreenAdCallback(closeWindowFun: () -> Unit) {
        (adBase.appAdDataYep as? InterstitialAd)?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdClicked() {
                }

                override fun onAdDismissedFullScreenContent() {
                    closeWindowFun()
                    adBase.appAdDataYep = null
                    adBase.whetherToShowYep = false
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    // Called when ad fails to show.
                    Log.d(TAG, "Ad failed to show fullscreen content.")
                    adBase.appAdDataYep = null
                    adBase.whetherToShowYep = false
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                }

                override fun onAdShowedFullScreenContent() {
                    adBase.appAdDataYep = null
                    // Called when ad is shown.
                    adBase.whetherToShowYep = true
                    Log.d(TAG, "connect----show")
                    adBase.yepAdBean = CloakUtils.afterLoadLink( adBase.yepAdBean)
                }
            }
    }


    fun displayConnectAdvertisementYep(
        activity: MainActivity,
        closeWindowFun: () -> Unit
    ): Int {
        val userData = AdUtils.blockAdUsers()
        val blacklistState = AdUtils.blockAdBlacklist()
        if (!blacklistState) {
            Log.d(TAG,"根据黑名单屏蔽插屏广告。。。")

            return 0
        }
        if (!userData) {
            Log.d(TAG,"根据买量屏蔽插屏广告。。。")
            return 0
        }

        if (adBase.appAdDataYep == null) {
            return 1
        }

        if (adBase.whetherToShowYep || activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
            return 1
        }
        connectScreenAdCallback(closeWindowFun)
        activity.lifecycleScope.launch(Dispatchers.Main) {
            if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                (adBase.appAdDataYep as InterstitialAd).show(activity)
            }
        }
        return 2
    }
}