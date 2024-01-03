package com.yep.online.link.prox.connection.view.ad
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
object YepLoadBackAd {
    private val adBase = BaseAdom.getBackInstance()

    fun loadBackAdvertisementYep(context: Context, adData: YepAdBean) {
        val adRequest = AdRequest.Builder().build()
        Log.d( TAG,"back--id=${adData.back}")
        adBase.yepAdBean = CloakUtils.beforeLoadLink(adData)
        InterstitialAd.load(
            context,
            adData.back,
            adRequest,
            interstitialAdLoadCallback(context)
        )
    }

    private fun interstitialAdLoadCallback(context: Context): InterstitialAdLoadCallback {
        return object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                val error =
                    """
           domain: ${adError.domain}, code: ${adError.code}, message: ${adError.message}
          """"
                adBase.isLoadingYep = false
                adBase.appAdDataYep = null
                CloakUtils.putPointTimeYep(
                    "adloaddissucc",
                    error,
                    "yn",
                    context
                )
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                adBase.loadTimeYep = Date().time
                adBase.isLoadingYep = false
                adBase.appAdDataYep = interstitialAd
                adBase.adIndexYep = 0
                Log.d(TAG,"back---返回插屏加载成功")
                interstitialAd.setOnPaidEventListener { adValue ->
                    YepOkHttpUtils().getAdList(context, adValue, interstitialAd.responseInfo, "back", adBase.yepAdBean)
                }
                CloakUtils.putPointTimeYep(
                    "adloadsucc",
                    "back",
                    "yn",
                    context
                )
            }
        }
    }

    private fun backScreenAdCallback(cloneAd:()->Unit) {
        (adBase.appAdDataYep  as? InterstitialAd)?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG,"back插屏广告点击")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(TAG,"关闭back插屏广告")
                adBase.appAdDataYep = null
                adBase.whetherToShowYep = false
                cloneAd()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.d(TAG,"Ad failed to show fullscreen content.")
                adBase.appAdDataYep = null
                adBase.whetherToShowYep = false
            }

            override fun onAdImpression() {
            }

            override fun onAdShowedFullScreenContent() {
                adBase.appAdDataYep = null
                adBase.whetherToShowYep = true
                Log.d(TAG,"back----show")
                adBase.yepAdBean = CloakUtils.afterLoadLink(adBase.yepAdBean)
            }
        }
    }

    fun displayBackAdvertisementYep(activity: AppCompatActivity,cloneAd:()->Unit): Int {

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
            Log.d(TAG,"back--插屏广告加载中。。。")
            return 1
        }
        if (adBase.whetherToShowYep || activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
            Log.d(TAG,"back--前一个插屏广告展示中或者生命周期不对")
            return 1
        }
        backScreenAdCallback(cloneAd)
        activity.lifecycleScope.launch(Dispatchers.Main) {
            (adBase.appAdDataYep as InterstitialAd).show(activity)
        }
        return 2
    }
}