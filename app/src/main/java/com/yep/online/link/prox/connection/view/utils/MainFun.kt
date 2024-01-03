package com.yep.online.link.prox.connection.view.utils

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.github.shadowsocks.database.Profile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yep.online.link.prox.connection.base.BaseAdom
import com.yep.online.link.prox.connection.hlep.DataUtils
import com.yep.online.link.prox.connection.hlep.DataUtils.TAG
import com.yep.online.link.prox.connection.hlep.Ip2Bean
import com.yep.online.link.prox.connection.hlep.IpBean
import com.yep.online.link.prox.connection.hlep.ServiceBean
import com.yep.online.link.prox.connection.net.CloakUtils
import com.yep.online.link.prox.connection.view.ui.EndActivity
import com.yep.online.link.prox.connection.view.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.system.exitProcess

object MainFun {
    fun setSkServerData(profile: Profile, bestData: ServiceBean): Profile {
        profile.name = bestData.country + "-" + bestData.city
        profile.host = bestData.ip
        profile.password = bestData.password
        profile.method = bestData.agreement
        if (bestData.port.isEmpty()) {
            profile.remotePort = 0
        } else {
            profile.remotePort = bestData.port.toInt()
        }
        return profile
    }

    private fun jumpConnectionResultsPage(isConnection: Boolean): Bundle {
        val bundle = Bundle()
        val serviceData = DataUtils.connect_vpn
        bundle.putBoolean(DataUtils.connectionYepStatus, isConnection)
        bundle.putString(DataUtils.serverYepInformation, serviceData)
        return bundle
    }

    fun jumpResultsPageData(activity: MainActivity, isConnection: Boolean) {
        activity.lifecycleScope.launch(Dispatchers.Main.immediate) {
            delay(300L)
            if (activity.lifecycle.currentState == Lifecycle.State.RESUMED) {
                BaseAdom.getBackInstance().advertisementLoadingYep(activity)
                activity.launchActivityWithExtrasResult(
                    EndActivity::class.java,
                    jumpConnectionResultsPage(isConnection),
                    0x22
                )
            }
        }
    }

    fun isLegalIpAddress(activity: AppCompatActivity): Boolean {
//        if (whetherParsingIsIllegalIp()) {
//            whetherTheBulletBoxCannotBeUsed(activity)
//            return true
//        }
        return false
    }

    fun whetherParsingIsIllegalIp(): Boolean {
        val data = DataUtils.ip_data
        Log.e(TAG, "whetherParsingIsIllegalIp: ${data}")
        return if (data.isEmpty()) {
            whetherParsingIsIllegalIp2()
        } else {
            val ptIpBean: IpBean = Gson().fromJson(
                data,
                object : TypeToken<IpBean?>() {}.type
            )
            return ptIpBean.country_code == "IR" || ptIpBean.country_code == "CN" ||
                    ptIpBean.country_code == "HK" || ptIpBean.country_code == "MO"
        }
    }

    private fun whetherParsingIsIllegalIp2(): Boolean {
        val data = DataUtils.ip_data2
        val locale = Locale.getDefault()
        val language = locale.language
        return if (data.isEmpty()) {
            language == "zh" || language == "fa"
        } else {
            val ptIpBean: Ip2Bean = Gson().fromJson(
                data,
                object : TypeToken<Ip2Bean?>() {}.type
            )
            return ptIpBean.cc == "IR" || ptIpBean.cc == "CN" ||
                    ptIpBean.cc == "HK" || ptIpBean.cc == "MO"
        }
    }

    /**
     * 是否显示不能使用弹框
     */
    fun whetherTheBulletBoxCannotBeUsed(context: AppCompatActivity) {
        CloakUtils.putPointYep("area", context)
        val dialogVpn: AlertDialog = AlertDialog.Builder(context)
            .setTitle("Tips")
            .setMessage("Due to the policy reason , this service is not available in your country")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                exitProcess(0)
            }.create()
        dialogVpn.setCancelable(false)
        dialogVpn.show()
        dialogVpn.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
        dialogVpn.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
    }

    fun isAppOnline(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        if (networkCapabilities != null) {
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
        return false
    }

    fun rotateImageViewInfinite(imageView: ImageView, duration: Long) {
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = duration
        rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.repeatMode = Animation.RESTART

        imageView.startAnimation(rotateAnimation)
    }

    fun stopRotation(imageView: ImageView) {
        imageView.clearAnimation()
    }
}