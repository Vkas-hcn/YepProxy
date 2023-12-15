package how.to.finish.the.project.tricevpn.view.ad

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import how.to.finish.the.project.tricevpn.base.BaseAdom
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG
import how.to.finish.the.project.tricevpn.hlep.YepAdBean
import java.util.Date
object YepLoadOpenAd {

    private val adBase = BaseAdom.getOpenInstance()
    var isFirstLoad: Boolean = false


    fun loadOpenAdYep(context: Context, adData: YepAdBean) {
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
                    Log.d(TAG, "open-加载成功 ")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    adBase.isLoadingYep = false
                    adBase.appAdDataYep = null
                    Log.d(TAG, "open-加载失败 ")

                    if (!isFirstLoad) {
                        adBase.advertisementLoadingYep(context)
                        isFirstLoad = true
                    }
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
                //取消全屏内容
                override fun onAdDismissedFullScreenContent() {
                    adBase.whetherToShowYep = false
                    adBase.appAdDataYep = null
                    fullScreenFun()
                }

                //全屏内容无法显示时调用
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    adBase.whetherToShowYep = false
                    adBase.appAdDataYep = null
                }

                //显示全屏内容时调用
                override fun onAdShowedFullScreenContent() {
                    adBase.appAdDataYep = null
                    adBase.whetherToShowYep = true
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                }
            }
    }


    fun displayOpenAdvertisementYep(activity: AppCompatActivity,fullScreenFun: () -> Unit): Boolean {
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