package how.to.finish.the.project.tricevpn.base
import android.content.Context
import android.util.Log
import how.to.finish.the.project.tricevpn.hlep.AdUtils
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG
import how.to.finish.the.project.tricevpn.hlep.ServiceData
import how.to.finish.the.project.tricevpn.hlep.YepAdBean
import how.to.finish.the.project.tricevpn.view.ad.YepLoadBackAd
import how.to.finish.the.project.tricevpn.view.ad.YepLoadConnectAd
import how.to.finish.the.project.tricevpn.view.ad.YepLoadEndAd
import how.to.finish.the.project.tricevpn.view.ad.YepLoadHomeAd
import how.to.finish.the.project.tricevpn.view.ad.YepLoadOpenAd
import java.util.*
class BaseAdom {
    companion object {
        fun getOpenInstance() = InstanceHelper.openLoadYep
        fun getHomeInstance() = InstanceHelper.homeLoadYep
        fun getResultInstance() = InstanceHelper.endLoadYep
        fun getConnectInstance() = InstanceHelper.connectLoadYep
        fun getBackInstance() = InstanceHelper.backLoadYep

        private var idCounter = 0
    }

    val id = ++idCounter

    object InstanceHelper {
        val openLoadYep = BaseAdom()
        val homeLoadYep = BaseAdom()
        val endLoadYep = BaseAdom()
        val connectLoadYep = BaseAdom()
        val backLoadYep = BaseAdom()
    }

    var appAdDataYep: Any? = null

    // 是否正在加载中
    var isLoadingYep = false

    //加载时间
    var loadTimeYep: Long = Date().time

    // 是否展示
    var whetherToShowYep = false
    // 是否屏蔽
    var whetherToBlockOrNotYep = false
    // openIndex
    var adIndexYep = 0

    // 是否是第一遍轮训
    var isFirstRotation: Boolean = false


    /**
     * 广告加载前判断
     */
    fun advertisementLoadingYep(context: Context) {


        if (isLoadingYep) {
            Log.d( TAG,"${getInstanceName()}--广告加载中，不能再次加载")
            return
        }
        val userData = AdUtils.blockAdUsers()
        val blacklistState = AdUtils.blockAdBlacklist()
        if (!blacklistState && (getInstanceName() == "connect" || getInstanceName() == "back")) {
            return
        }
        if (!userData && (getInstanceName() == "connect" || getInstanceName() == "back" || getInstanceName() == "home")) {
            return
        }

        isFirstRotation = false

        if (appAdDataYep == null) {
            isLoadingYep = true
            Log.d(  TAG,"${getInstanceName()}--广告开始加载")
            loadStartupPageAdvertisementYep(context, ServiceData.getAdJson())
        }

        if (appAdDataYep != null && !whetherAdExceedsOneHour(loadTimeYep)) {
            isLoadingYep = true
            appAdDataYep = null
            Log.d( TAG,"${getInstanceName()}--广告过期重新加载")
            loadStartupPageAdvertisementYep(context, ServiceData.getAdJson())
        }
    }

    /**
     * 广告是否超过过期（false:过期；true：未过期）
     */
    private fun whetherAdExceedsOneHour(loadTime: Long): Boolean =
        Date().time - loadTime < 60 * 60 * 1000


    /**
     * 加载启动页广告
     */
    private fun loadStartupPageAdvertisementYep(context: Context, adData: YepAdBean) {
        adLoaders[id]?.invoke(context, adData)
    }

    private val adLoaders = mapOf<Int, (Context, YepAdBean) -> Unit>(
        1 to ::loadOpenAdYep,
        2 to ::loadHomeAdYep,
        3 to ::loadResultAdYep,
        4 to ::loadConnectAdYep,
        5 to ::loadBackAdYep,
    )

    /**
     * 加载"open"类型广告
     */
    private fun loadOpenAdYep(context: Context, adData: YepAdBean) {
            YepLoadOpenAd.loadOpenAdYep(context, adData)
    }

    /**
     * 加载"home"类型广告
     */
    private fun loadHomeAdYep(context: Context, adData: YepAdBean) {
        YepLoadHomeAd.loadHomeAdvertisementYep(context, adData)
    }

    /**
     * 加载"end"类型广告
     */
    private fun loadResultAdYep(context: Context, adData: YepAdBean) {
        YepLoadEndAd.loadEndAdvertisementYep(context, adData)
    }

    /**
     * 加载"connect"类型广告
     */
    private fun loadConnectAdYep(context: Context, adData: YepAdBean) {
        YepLoadConnectAd.loadConnectAdvertisementYep(context, adData)
    }

    /**
     * 加载"back"类型广告
     */
    private fun loadBackAdYep(context: Context, adData: YepAdBean) {
        YepLoadBackAd.loadBackAdvertisementYep(context, adData)
    }


    /**
     * 获取实例名称
     */
    private fun getInstanceName(): String {
        return when (id) {
            1 -> "open"
            2 -> "home"
            3 -> "end"
            4 -> "connect"
            5 -> "back"
            else -> ""
        }
    }
}