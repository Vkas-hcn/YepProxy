package com.yep.online.link.prox.connection.base

import android.content.Context
import android.util.Log
import com.yep.online.link.prox.connection.hlep.AdUtils
import com.yep.online.link.prox.connection.hlep.DataUtils.TAG
import com.yep.online.link.prox.connection.hlep.ServiceData
import com.yep.online.link.prox.connection.hlep.YepAdBean
import com.yep.online.link.prox.connection.net.CloakUtils
import com.yep.online.link.prox.connection.view.ad.YepLoadBackAd
import com.yep.online.link.prox.connection.view.ad.YepLoadConnectAd
import com.yep.online.link.prox.connection.view.ad.YepLoadEndAd
import com.yep.online.link.prox.connection.view.ad.YepLoadHomeAd
import com.yep.online.link.prox.connection.view.ad.YepLoadOpenAd
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
    var isLoadingYep = false

    var loadTimeYep: Long = Date().time
    var whetherToShowYep = false

    var adIndexYep = 0

    var isFirstRotation: Boolean = false

    var yepAdBean: YepAdBean = ServiceData.getAdJson()

    fun advertisementLoadingYep(context: Context) {
        if (isLoadingYep) {
            Log.d(TAG, "${getInstanceName()}--广告加载中，不能再次加载")
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
            Log.d(TAG, "${getInstanceName()}--广告开始加载")
            loadStartupPageAdvertisementYep(context, ServiceData.getAdJson())
        }

        if (appAdDataYep != null && !whetherAdExceedsOneHour(loadTimeYep)) {
            isLoadingYep = true
            appAdDataYep = null
            Log.d(TAG, "${getInstanceName()}--广告过期重新加载")
            loadStartupPageAdvertisementYep(context, ServiceData.getAdJson())
        }
    }

    private fun whetherAdExceedsOneHour(loadTime: Long): Boolean =
        Date().time - loadTime < 60 * 60 * 1000

    private fun loadStartupPageAdvertisementYep(context: Context, adData: YepAdBean) {
        adLoaders[id]?.invoke(context, adData)
        CloakUtils.putPointTimeYep(
            "adload",
            getInstanceName(),
            "yn",
            context
        )
    }

    private val adLoaders = mapOf<Int, (Context, YepAdBean) -> Unit>(
        1 to ::loadOpenAdYep,
        2 to ::loadHomeAdYep,
        3 to ::loadResultAdYep,
        4 to ::loadConnectAdYep,
        5 to ::loadBackAdYep,
    )


    private fun loadOpenAdYep(context: Context, adData: YepAdBean) {
        YepLoadOpenAd.loadOpenAdYep(context, adData)
    }


    private fun loadHomeAdYep(context: Context, adData: YepAdBean) {
        YepLoadHomeAd.loadHomeAdvertisementYep(context, adData)
    }


    private fun loadResultAdYep(context: Context, adData: YepAdBean) {
        YepLoadEndAd.loadEndAdvertisementYep(context, adData)
    }

    private fun loadConnectAdYep(context: Context, adData: YepAdBean) {
        YepLoadConnectAd.loadConnectAdvertisementYep(context, adData)
    }

    private fun loadBackAdYep(context: Context, adData: YepAdBean) {
        YepLoadBackAd.loadBackAdvertisementYep(context, adData)
    }

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