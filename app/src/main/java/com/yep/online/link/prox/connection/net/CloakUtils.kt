package com.yep.online.link.prox.connection.net

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.android.installreferrer.api.ReferrerDetails
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.ResponseInfo
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.yep.online.link.prox.connection.hlep.AdType
import com.yep.online.link.prox.connection.hlep.DataUtils
import com.yep.online.link.prox.connection.hlep.DataUtils.TAG
import com.yep.online.link.prox.connection.hlep.ServiceData
import com.yep.online.link.prox.connection.hlep.YepAdBean
import org.json.JSONObject
import java.util.Locale
import java.util.TimeZone

object CloakUtils {

    @SuppressLint("HardwareIds")
    private fun createJsonData(context: Context): JSONObject {
        val jsonData = JSONObject()
        // hatch
        val hatch = JSONObject()
        //brand
        hatch.put("aba", Build.BRAND)
        //zone_offset
        hatch.put("denny", TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000)
        //system_language
        hatch.put("dublin", "${Locale.getDefault().language}_${Locale.getDefault().country}")
        //network_type
        hatch.put("roam", "")
        //bundle_id
        hatch.put("site", context.packageName)
        //channel
        hatch.put("taper", "")
        jsonData.put("hatch", hatch)

        // guthrie
        val guthrie = JSONObject()
        //app_version
        guthrie.put("sickle", getAppVersion(context))
        //device_model
        guthrie.put("adopt", Build.MODEL)
        //os_version
        guthrie.put("aiken", Build.VERSION.RELEASE)
        jsonData.put("guthrie", guthrie)

        // cocoa
        val cocoa = JSONObject()
        //log_id
        cocoa.put("arcsin", DataUtils.uu0d_yep)
        //cpu_name
        cocoa.put("pursue", Build.SUPPORTED_ABIS.joinToString(", "))
        //os
        cocoa.put("dennis", "acrid")
        //android_id
        cocoa.put(
            "terrify",
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        )
        //ip
        cocoa.put("dont", DataUtils.ip_tab)
        //client_ts
        cocoa.put("pariah", System.currentTimeMillis())
        //gaid
        cocoa.put("taxicab", "")
        //operator
        cocoa.put("slurp", getNetworkInfo(context))
        //key
        cocoa.put("calabash", DataUtils.uu0d_yep)
        //manufacturer
        cocoa.put("taylor", Build.MODEL)
        jsonData.put("cocoa", cocoa)

        // loanword
        val loanword = JSONObject()
        //ab_test
        loanword.put("liable", "")
        //screen_res
        loanword.put("full", getScreenResolution(context))
        //distinct_id
        loanword.put("railway", DataUtils.uu0d_yep)
        //os_country
        loanword.put("year", Locale.getDefault().country)
        jsonData.put("loanword", loanword)

        return jsonData
    }

    fun getSessionJson(context: Context): String {
        val topLevelJson = createJsonData(context)
        topLevelJson.apply {
            put("sigmund", "swampy")
        }
        return topLevelJson.toString()
    }

    fun getInstallJson(rd: ReferrerDetails, context: Context): String {
        val topLevelJson = createJsonData(context)

        topLevelJson.apply {
            //build
            put("junta", "build/${Build.ID}")

            //referrer_url
            put("crocus", rd.installReferrer)

            //install_version
            put("ditto", rd.installVersion)

            //user_agent
            put("antic", getWebDefaultUserAgent(context))

            //lat
            put("mcadams", getLimitTracking(context))

            //referrer_click_timestamp_seconds
            put("fallow", rd.referrerClickTimestampSeconds)

            //install_begin_timestamp_seconds
            put("moliere", rd.installBeginTimestampSeconds)

            //referrer_click_timestamp_server_seconds
            put("beijing", rd.referrerClickTimestampServerSeconds)

            //install_begin_timestamp_server_seconds
            put("diaper", rd.installBeginTimestampServerSeconds)

            //install_first_seconds
            put("mt", getFirstInstallTime(context))

            //last_update_seconds
            put("egyptian", getLastUpdateTime(context))

            //google_play_instant
            put("oratoric", rd.googlePlayInstantParam)
        }
        return topLevelJson.toString()
    }

    fun getAdJson(
        context: Context, adValue: AdValue,
        responseInfo: ResponseInfo,
        type: String,
        yepAdBean: YepAdBean
    ): String {
        val topLevelJson = createJsonData(context)
        val hemlock = JSONObject()
        hemlock.put("ad_network", responseInfo.mediationAdapterClassName)
            //ad_pre_ecpm
        hemlock.put("devise", adValue.valueMicros)
            //currency
        hemlock.put("collapse", adValue.currencyCode)
            //ad_network
        hemlock.put(
                "knowlton",
                responseInfo.mediationAdapterClassName
            )
            //ad_source
        hemlock.put("caliper", "admob")
            //ad_code_id
        hemlock.put("score", getAdType(type).id)
            //ad_pos_id
        hemlock.put("posable", getAdType(type).name)
            //ad_rit_id
        hemlock.put("drunken", "")
            //ad_sense
        hemlock.put("wee", "")
            //ad_format
        hemlock.put("jubilate", getAdType(type).type)
            //precision_type
        hemlock.put("holeable", getPrecisionType(adValue.precisionType))
            //ad_load_ip
        hemlock.put("woodlot", yepAdBean.loadIp ?: "")
            //ad_impression_ip
        hemlock.put("lansing", yepAdBean.showIp ?: "")
        hemlock.put("david@rid_yawn", yepAdBean.loadCity)
        hemlock.put("david@rid_fist", yepAdBean.showTheCity)
            //ad_sdk_ver
        hemlock.put("impale", responseInfo.responseId)
        topLevelJson.put("hemlock", hemlock)
        return topLevelJson.toString()
    }
    fun getTbaDataJson(context: Context,name:String): String {
        return createJsonData(context).apply {
            put("sigmund", name)
        }.toString()
    }
    fun getTbaTimeDataJson(context: Context,time:Any,name:String,parameterName:String): String {
        val data = JSONObject()
        data.put(parameterName, time)
        return createJsonData(context).apply {
            put("sigmund",name)
            put("${parameterName}_sinewy",time)
        }.toString()
    }
    @SuppressLint("HardwareIds")
    fun cloakJson(activity: AppCompatActivity): Map<String, Any> {
        return mapOf<String, Any>(
            //distinct_id
            "railway" to (DataUtils.uu0d_yep),
            //client_ts
            "pariah" to (System.currentTimeMillis()),
            //device_model
            "adopt" to Build.MODEL,
            //bundle_id
            "site" to ("com.easy.online.link.prox.connection"),
            //os_version
            "aiken" to Build.VERSION.RELEASE,
            //gaid
            "taxicab" to "",
            //android_id
            "terrify" to Settings.Secure.getString(
                activity.contentResolver,
                Settings.Secure.ANDROID_ID
            ),
            //os
            "dennis" to "acrid",
            //app_version
            "sickle" to getAppVersion(activity),//应用的版本
            //brand
            "aba" to ((activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperator)
        )
    }

    private fun getAppVersion(context: Context): String {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "Version information not available"
    }


    private fun getNetworkInfo(context: Context): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // 获取网络供应商名称
        val carrierName = telephonyManager.networkOperatorName

        // 获取 MCC 和 MNC
        val networkOperator = telephonyManager.networkOperator
        val mcc = if (networkOperator.length >= 3) networkOperator.substring(0, 3) else ""
        val mnc = if (networkOperator.length >= 5) networkOperator.substring(3) else ""

        return """
        Carrier Name: $carrierName
        MCC: $mcc
        MNC: $mnc
    """.trimIndent()
    }

    private fun getScreenResolution(context: Context): String {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()

        // 获取屏幕宽度和高度
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        return "$screenWidth * $screenHeight"
    }

    private fun getWebDefaultUserAgent(context: Context): String {
        return try {
            WebSettings.getDefaultUserAgent(context)
        } catch (e: Exception) {
            ""
        }
    }

    private fun getLimitTracking(context: Context): String {
        return try {
            if (AdvertisingIdClient.getAdvertisingIdInfo(context).isLimitAdTrackingEnabled) {
                "february"
            } else {
                "sept"
            }
        } catch (e: Exception) {
            "sept"
        }
    }

    private fun getFirstInstallTime(context: Context): Long {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.firstInstallTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }

    private fun getLastUpdateTime(context: Context): Long {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.lastUpdateTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }


    private fun getAdType(type: String): AdType {
        var adType = AdType("", "", "", "")
        val adData = ServiceData.getAdJson()
        when (type) {
            "open" -> {
                adType = AdType(adData.open, "open", "ousyet", "open")
            }

            "home" -> {
                adType = AdType(adData.home, "home", "bedyet", "native")
            }

            "end" -> {
                adType = AdType(adData.end, "end", "queyet", "native")
            }

            "connect" -> {
                adType = AdType(adData.connect, "connect", "tranyet", "interstitial")
            }

            "back" -> {
                adType = AdType(adData.back, "back", "furtyet", "interstitial")
            }
        }
        return adType
    }

    private fun getPrecisionType(precisionType: Int): String {
        return when (precisionType) {
            0 -> {
                "UNKNOWN"
            }

            1 -> {
                "ESTIMATED"
            }

            2 -> {
                "PUBLISHER_PROVIDED"
            }

            3 -> {
                "PRECISE"
            }

            else -> {
                "UNKNOWN"
            }
        }
    }

    fun beforeLoadLink(yepAdBean: YepAdBean): YepAdBean {
        val ipAfterVpnLink = DataUtils.vpn_ip
        val ipAfterVpnCity = DataUtils.vpn_city
        if (com.yep.online.link.prox.connection.base.App.vpnState) {
            yepAdBean.loadIp = ipAfterVpnLink ?: ""
            yepAdBean.loadCity = ipAfterVpnCity ?: ""
        } else {
            yepAdBean.loadIp = DataUtils.ip_tab
            yepAdBean.loadCity = "null"
        }
        return yepAdBean
    }
    fun afterLoadLink(yepAdBean: YepAdBean): YepAdBean {
        val ipAfterVpnLink = DataUtils.vpn_ip
        val ipAfterVpnCity = DataUtils.vpn_city
        if (com.yep.online.link.prox.connection.base.App.vpnState) {
            yepAdBean.showIp = ipAfterVpnLink ?: ""
            yepAdBean.showTheCity = ipAfterVpnCity ?: ""
        } else {
            yepAdBean.showIp = DataUtils.ip_tab
            yepAdBean.showTheCity = "null"
        }
        return yepAdBean
    }

    fun putPointYep(name: String,context: Context) {
        YepOkHttpUtils().getTbaList(context,name)
        if (com.yep.online.link.prox.connection.BuildConfig.DEBUG) {
            Log.d(TAG,"触发埋点----name=${name}")
        } else {
            Firebase.analytics.logEvent(name, null)
        }
    }

    fun putPointTimeYep(name: String, time: Any,parameterName:String,context: Context) {
        YepOkHttpUtils().getTbaList(context,name,parameterName, time, 1)
        if (com.yep.online.link.prox.connection.BuildConfig.DEBUG) {
            Log.d(TAG,"触发埋点----name=${name}---time=${time}")
        } else {
            Firebase.analytics.logEvent(name, bundleOf(parameterName to time))
        }
    }
}