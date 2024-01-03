package com.yep.online.link.prox.connection.view.ad

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.yep.online.link.prox.connection.base.BaseAdom
import com.yep.online.link.prox.connection.hlep.DataUtils.TAG
import com.yep.online.link.prox.connection.hlep.YepAdBean
import com.yep.online.link.prox.connection.net.CloakUtils
import com.yep.online.link.prox.connection.net.YepOkHttpUtils
import java.util.Date

object YepLoadOpenAd {

    private val adBase = BaseAdom.getOpenInstance()
    var isFirstLoad: Boolean = false
    fun loadOpenAdYep(context: Context, adData: YepAdBean) {
        adBase.yepAdBean = CloakUtils.beforeLoadLink(adData)
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            adData.open,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    adBase.isLoadingYep = false
                    adBase.appAdDataYep = ad
                    adBase.loadTimeYep = Date().time
                    Log.d(TAG, "open-Loaded successfully")
                    ad.setOnPaidEventListener { adValue ->
                        Log.e(TAG, "App open ads start reporting")
                        adValue.let {
                            YepOkHttpUtils().getAdList(
                                context,
                                adValue,
                                ad.responseInfo,
                                "open",
                                adBase.yepAdBean
                            )
                        }
                    }
                    CloakUtils.putPointTimeYep(
                        "adloadsucc",
                        "open",
                        "yn",
                        context
                    )
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adBase.isLoadingYep = false
                    adBase.appAdDataYep = null
                    val error =
                        """
           domain: ${adError.domain}, code: ${adError.code}, message: ${adError.message}
          """"
                    if (!isFirstLoad) {
                        adBase.advertisementLoadingYep(context)
                        isFirstLoad = true
                    }
                    CloakUtils.putPointTimeYep(
                        "adloaddissucc",
                        error,
                        "yn",
                        context
                    )
                }
            }
        )
    }


    private fun advertisingOpenCallbackYep(fullScreenFun: () -> Unit) {
        if (adBase.appAdDataYep !is AppOpenAd) {
            return
        }
        (adBase.appAdDataYep as AppOpenAd).fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    adBase.whetherToShowYep = false
                    adBase.appAdDataYep = null
                    fullScreenFun()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    adBase.whetherToShowYep = false
                    adBase.appAdDataYep = null
                }

                override fun onAdShowedFullScreenContent() {
                    adBase.appAdDataYep = null
                    adBase.whetherToShowYep = true
                    adBase.yepAdBean = CloakUtils.afterLoadLink(adBase.yepAdBean)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                }
            }
    }


    fun displayOpenAdvertisementYep(
        activity: AppCompatActivity,
        fullScreenFun: () -> Unit
    ): Boolean {
        if (adBase.appAdDataYep == null) {
            return false
        }
        if (adBase.whetherToShowYep || activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
            return false
        }
        advertisingOpenCallbackYep(fullScreenFun)
        (adBase.appAdDataYep as AppOpenAd).show(activity)
        return true
    }
}