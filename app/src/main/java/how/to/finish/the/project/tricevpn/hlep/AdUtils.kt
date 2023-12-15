package how.to.finish.the.project.tricevpn.hlep

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import how.to.finish.the.project.tricevpn.BuildConfig
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

object AdUtils {
    private fun isFacebookUser(): Boolean {
        val data = ServiceData.getUserJson()
        val referrer = DataUtils.refer_data
        val pattern = "fb4a|facebook".toRegex(RegexOption.IGNORE_CASE)
        return (pattern.containsMatchIn(referrer) && data.onLleav == "1")
    }

    fun isItABuyingUser(): Boolean {
        val data =  ServiceData.getUserJson()
        val referrer = DataUtils.refer_data
        return isFacebookUser()
                || (data.onLeate == "1" && referrer.contains("gclid", true))
                || (data.onLmill == "1" && referrer.contains("not%20set", true))
                || (data.onLage == "1" && referrer.contains(
            "youtubeads",
            true
        ))
                || (data.onLiden == "1" && referrer.contains("%7B%22", true))
                || (data.onLclem == "1" && referrer.contains("adjust", true))
                || (data.onLisp == "1" && referrer.contains("bytedance", true))
    }

    //屏蔽广告用户
    fun blockAdUsers():Boolean{
        when(ServiceData.getLogicJson().onLmatt){
            "1"->{
                return true
            }
            "2"->{
                return isItABuyingUser()
            }
            "3"->{
                return false
            }
            else->{
                return true
            }
        }
    }
    //黑名单
    fun blockAdBlacklist():Boolean{
        val blackData = !DataUtils.clock_data.contains("mummify")
        when(ServiceData.getLogicJson().onLprob){
            "1"->{
                return !blackData
            }
            "2"->{
                return true
            }
            else->{
                return true
            }
        }
    }
    //是否扰流
    fun spoilerOrNot():Boolean{
        when(ServiceData.getLogicJson().onLfeli){
            "1"->{
                return true
            }
            "2"->{
                return false
            }
            "3"->{
                return !isItABuyingUser()
            }
            else->{
                return false
            }
        }
    }

    fun getFileBaseData(activity: AppCompatActivity,loadAdFun:()->Unit) {
       activity.lifecycleScope.launch {
            var isCa = false
            if (!BuildConfig.DEBUG) {
                val auth = Firebase.remoteConfig
                auth.fetchAndActivate().addOnSuccessListener {
                    DataUtils.vpn_list = auth.getString(DataUtils.vpn_data_type)
                    DataUtils.vpn_fast = auth.getString(DataUtils.fast_data_type)
                    DataUtils.ad_data = auth.getString(DataUtils.ad_data_type)
                    DataUtils.user_data = auth.getString(DataUtils.user_data_type)
                    DataUtils.lj_data = auth.getString(DataUtils.lj_data_type)
                    isCa = true
                }
            }
            try {
                withTimeout(4000L) {
                    while (true) {
                        if (!isActive) {
                            break
                        }
                        if (isCa) {
                            loadAdFun()
                            cancel()
                        }
                        delay(500)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                cancel()
                loadAdFun()
            }
        }
    }

}