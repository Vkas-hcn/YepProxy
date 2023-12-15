package how.to.finish.the.project.tricevpn.view.ad

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
import how.to.finish.the.project.tricevpn.base.BaseAdom
import how.to.finish.the.project.tricevpn.hlep.AdUtils
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG
import how.to.finish.the.project.tricevpn.hlep.YepAdBean
import how.to.finish.the.project.tricevpn.view.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
object YepLoadConnectAd {
    private val adBase = BaseAdom.getConnectInstance()
    fun loadConnectAdvertisementYep(context: Context, adData: YepAdBean) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            adData.connect,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adBase.isLoadingYep = false
                    adBase.appAdDataYep = null
                    Log.d(TAG, "connect-加载失败 ")

                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    adBase.loadTimeYep = Date().time
                    adBase.isLoadingYep = false
                    adBase.appAdDataYep = interstitialAd
                    Log.d(TAG, "connect-加载成功 ")

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