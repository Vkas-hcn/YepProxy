package how.to.finish.the.project.tricevpn.net

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.provider.Settings
import android.telephony.TelephonyManager
import how.to.finish.the.project.tricevpn.hlep.DataUtils

object CloakUtils {
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
            "terrify" to Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID),
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
}