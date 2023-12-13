package how.to.finish.the.project.tricevpn.baseeeeee

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import how.to.finish.the.project.tricevpn.uitlllll.SPUtil
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
object AppConstant {
    const val web_page = "https://blog.csdn.net/LoveFHM?type=blog"
    const val black_url = "https://bifocal.tricestable.com/shrub/clog/xerox"
    const val IS_CONNECT = "is_connect"
    var userInterrupt = false
    var isResultState = false

    var lock_code: String
        get() {
            val pos = SPUtil(BaseApp.getInstance().applicationContext).getString(
                "lock_code",
                ""
            )
            return pos
        }
        set(value) {
            SPUtil(BaseApp.getInstance().applicationContext).putString(
                "lock_code",
                value
            )
        }
    var uuid_trice: String
        get() {
            val pos = SPUtil(BaseApp.getInstance().applicationContext).getString(
                "uuid_trice",
                ""
            )
            return pos
        }
        set(value) {
            SPUtil(BaseApp.getInstance().applicationContext).putString(
                "uuid_trice",
                value
            )
        }
    var black_trice: String
        get() {
            val pos = SPUtil(BaseApp.getInstance().applicationContext).getString(
                "black_trice",
                ""
            )
            return pos
        }
        set(value) {
            SPUtil(BaseApp.getInstance().applicationContext).putString(
                "black_trice",
                value
            )
        }

    fun cloakData(context: Context): Map<String, String> {
        return mapOf(
            "tootle" to uuid_trice, // distinct_id
            "sadie" to System.currentTimeMillis().toString(), // client_ts
            "barstow" to Build.MODEL,//device_model
            "bulky" to "com.trice.stable.safetool",// bundle_id
            "bronx" to Build.VERSION.RELEASE, // os_version
            "rufous" to "", // gaid
            "cowpunch" to getAndroidId(context), // android_id
            "cheerful" to "roland", // os
            "ferric" to getAppVersion(context), // app_version
            "dawdle" to getNetworkType(context), // network_type
        )
    }
    private fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName
            versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }


    @SuppressLint("SuspiciousIndentation")
    fun getNetworkType(context: Context): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork =
            connectivityManager.activeNetwork ?: return "No network"
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return when {
//            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
//            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
//            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Unknown"
        }
    }

}