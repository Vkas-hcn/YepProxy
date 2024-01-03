package com.yep.online.link.prox.connection.base

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.yep.online.link.prox.connection.view.ui.StartActivity
import com.tencent.mmkv.MMKV
import android.os.Process
import android.webkit.WebView
import com.github.shadowsocks.Core
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.yep.online.link.prox.connection.hlep.DataUtils
import com.yep.online.link.prox.connection.hlep.YepTimerUtils
import com.yep.online.link.prox.connection.view.ui.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.yep.online.link.prox.connection.net.CloakUtils
import com.yep.online.link.prox.connection.net.YepOkHttpUtils

class App : Application(), Application.ActivityLifecycleCallbacks {
    companion object {
        var num = 0
        var nums = mutableListOf<Activity>()
        var timeData = 0L
        var startState: Boolean = false
        var vpnState: Boolean = false
        val mmkvDua by lazy {
            MMKV.mmkvWithID("Dual", MMKV.MULTI_PROCESS_MODE)
        }
        var serviceState:Int = -1
    }

    var adActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        Core.init(this, MainActivity::class)
        val myPid = Process.myPid()
        val activityManager =
            this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val processInfoList = activityManager.runningAppProcesses
        val packageName = this.packageName
        for (info in processInfoList) {
            if (info!!.pid == myPid && packageName == info.processName) {
               YepTimerUtils. sendTimerInformation()
                registerActivityLifecycleCallbacks(this)
                MobileAds.initialize(this) {}
                Firebase.initialize(this)
                FirebaseApp.initializeApp(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (this.packageName != getProcessName()) {
                        WebView.setDataDirectorySuffix(getProcessName())
                    }
                }
                val data = DataUtils.uu0d_yep
                if (data.isEmpty()) {
                    DataUtils.uu0d_yep = UUID.randomUUID().toString()
                }
                getReferInformation(this)
                CloakUtils.putPointYep("coldstart", this)
            }
        }

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        nums.add(activity)

    }

    override fun onActivityStarted(activity: Activity) {
        num++
        if (activity is AdActivity) {
            adActivity = activity
        }
        if (startState) {
            startState = false
            if ((System.currentTimeMillis() - timeData) / 1000 > 3) {
                val intent = Intent(activity, StartActivity::class.java)
                activity.startActivity(intent)
                if (adActivity != null) adActivity?.finish()
                if (activity is StartActivity) activity.finish()
                BaseAdom.getHomeInstance().whetherToShowYep = false
                BaseAdom.getResultInstance().whetherToShowYep = false
                CloakUtils.putPointYep("hotStart", activity)
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        num--
        if (num == 0) {
            startState = true
            timeData = System.currentTimeMillis()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        Companion.nums.remove(activity)
    }

    private fun getReferInformation(context: Context) {
         GlobalScope.launch {
            while (isActive) {
                if (DataUtils.refer_data.isEmpty()) {
                    getReferrerData(context)
                } else {
                    cancel()
                }
                delay(5000)
            }
        }
    }

    private fun getReferrerData(context: Context) {
        var date = System.currentTimeMillis()
        var installReferrer =""
        val referrer = DataUtils.refer_data
        if (referrer.isNotBlank()) {
            return
        }
//        installReferrer = "gclid"
//        installReferrer = "fb4a"
//        DataUtils.refer_data = installReferrer
        runCatching {
            val referrerClient = InstallReferrerClient.newBuilder(context).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(p0: Int) {
                    when (p0) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            val installReferrer =
                                referrerClient.installReferrer.installReferrer ?: ""
                            DataUtils.refer_data = installReferrer
                           val loadDate = (System.currentTimeMillis()-date)/1000
                            CloakUtils.putPointTimeYep("reftime",loadDate.toInt(),"conntime",context)
                            if (!DataUtils.refer_tab) {
                                runCatching {
                                    referrerClient?.installReferrer?.run {
                                        YepOkHttpUtils().getInstallList(context, this)
                                    }
                                }.exceptionOrNull()
                            }
                        }
                    }
                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {
                }
            })
        }.onFailure { e ->
            // 处理异常
        }
    }
}
