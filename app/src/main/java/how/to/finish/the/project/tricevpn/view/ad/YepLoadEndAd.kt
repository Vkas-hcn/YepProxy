package how.to.finish.the.project.tricevpn.view.ad
import android.content.Context
import android.graphics.Outline
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.base.BaseAdom
import how.to.finish.the.project.tricevpn.hlep.DataUtils
import how.to.finish.the.project.tricevpn.hlep.YepAdBean
import how.to.finish.the.project.tricevpn.view.ui.EndActivity
import java.util.Date
object YepLoadEndAd {
    private val adBase = BaseAdom.getResultInstance()
    fun loadEndAdvertisementYep(context: Context, adData: YepAdBean) {

        val vpnNativeAds = AdLoader.Builder(
            context.applicationContext,
            adData.end
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
        }
        vpnNativeAds.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                val error =
                    """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
                adBase.isLoadingYep = false
                adBase.appAdDataYep = null
                Log.d(DataUtils.TAG, "end-加载失败 ")

            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                adBase.loadTimeYep = Date().time
                adBase.isLoadingYep = false
                Log.d(DataUtils.TAG, "end-加载成功 ")

            }

            override fun onAdOpened() {
                super.onAdOpened()
            }
        }).build().loadAd(AdRequest.Builder().build())
    }


    fun setDisplayEndNativeAdYep(activity: EndActivity) {
        activity.runOnUiThread {
            val binding = activity.binding
            adBase.appAdDataYep?.let { adData ->
                val state = activity.lifecycle.currentState == Lifecycle.State.RESUMED

                if (adData is NativeAd && !adBase.whetherToShowYep && state) {
                    binding.showAd = false

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
                    binding.showAd =true
                    adBase.whetherToShowYep = true
                    adBase.appAdDataYep = null
                    Log.d(DataUtils.TAG,"end广告-show。。。")
                    BaseAdom.getResultInstance().advertisementLoadingYep(activity)

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
//        adView.mediaView?.outlineProvider = R8()
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