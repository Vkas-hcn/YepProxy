package com.yep.online.link.prox.connection.view.ad
import android.content.Context
import android.graphics.Outline
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.yep.online.link.prox.connection.R
import com.yep.online.link.prox.connection.base.BaseAdom
import com.yep.online.link.prox.connection.hlep.AdUtils
import com.yep.online.link.prox.connection.hlep.DataUtils.TAG
import com.yep.online.link.prox.connection.hlep.YepAdBean
import com.yep.online.link.prox.connection.net.CloakUtils
import com.yep.online.link.prox.connection.net.YepOkHttpUtils
import com.yep.online.link.prox.connection.view.ui.MainActivity
import java.util.Date
object YepLoadHomeAd {
    private val adBase = BaseAdom.getHomeInstance()
    fun loadHomeAdvertisementYep(context: Context, adData: YepAdBean) {
        adBase.yepAdBean = CloakUtils.beforeLoadLink(adData)
        val vpnNativeAds = AdLoader.Builder(
            context.applicationContext,
            adData.home
        )
        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_LEFT)
            .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_PORTRAIT)
            .build()

        vpnNativeAds.withNativeAdOptions(adOptions)
        vpnNativeAds.forNativeAd {
            adBase.appAdDataYep = it
            it.setOnPaidEventListener { adValue ->
                it.responseInfo?.let { nav ->
                    YepOkHttpUtils().getAdList(context, adValue, nav, "home", adBase.yepAdBean)
                }
                BaseAdom.getHomeInstance().advertisementLoadingYep(context)
            }
        }
        vpnNativeAds.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                adBase.isLoadingYep = false
                adBase.appAdDataYep = null
                Log.d(TAG, "home-Failed to load ")
                CloakUtils.putPointTimeYep(
                    "adloaddissucc",
                    "home",
                    "yn",
                    context
                )
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(TAG, "home-Loaded successfully")
                adBase.loadTimeYep = Date().time
                adBase.isLoadingYep = false
                CloakUtils.putPointTimeYep(
                    "adloadsucc",
                    "home",
                    "yn",
                    context
                )
            }

            override fun onAdOpened() {
                super.onAdOpened()
            }
        }).build().loadAd(AdRequest.Builder().build())
    }


    fun setDisplayHomeNativeAdYep(activity: MainActivity) {
        activity.runOnUiThread {
            val binding = activity.binding
            adBase.appAdDataYep?.let { adData ->
                val state = activity.lifecycle.currentState == Lifecycle.State.RESUMED
                if (adData is NativeAd && !adBase.whetherToShowYep && state) {
                    val userData = AdUtils.blockAdUsers()
                    if (!userData) {
                        Log.d(TAG,"根据买量屏蔽home广告。。。")
                        binding.showAd = 0
                        return@let
                    }
                    binding.showAd = 1

                    if (activity.isDestroyed || activity.isFinishing || activity.isChangingConfigurations) {
                        adData.destroy()
                        return@let
                    }
                    val adView = activity.layoutInflater.inflate(
                        R.layout.layout_ad,
                        null
                    ) as NativeAdView
                    // 对应原生组件
                    setCorrespondingNativeComponentYep(adData, adView)
                    binding.flAd.apply {
                        removeAllViews()
                        addView(adView)
                    }
                    binding.showAd = 2
                    adBase.whetherToShowYep = true
                    adBase.appAdDataYep = null
                    Log.d(TAG,"home----show")
                   adBase.yepAdBean = CloakUtils.afterLoadLink(adBase.yepAdBean)

                }
            }
        }
    }

    private fun setCorrespondingNativeComponentYep(nativeAd: NativeAd, adView: NativeAdView) {
        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)

        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        (adView.headlineView as TextView).text = nativeAd.headline
        nativeAd.mediaContent?.let {
            adView.mediaView?.apply { setImageScaleType(ImageView.ScaleType.CENTER_CROP) }?.setMediaContent(it)
        }
        adView.mediaView?.clipToOutline = true
        adView.mediaView?.outlineProvider = R8()
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as TextView).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }
}

class R8 : ViewOutlineProvider() {
    override fun getOutline(view: View?, outline: Outline?) {
        val sView = view ?: return
        val sOutline = outline ?: return
        sOutline.setRoundRect(
            0,
            0,
            sView.width,
            sView.height,
            8f
        )
    }
}