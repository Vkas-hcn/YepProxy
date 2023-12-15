package com.github.shadowsocks.bg

import android.util.Log
import com.tencent.mmkv.MMKV
import android.net.VpnService

object RlUtils {
    private val mmkv by lazy {
        MMKV.mmkvWithID("Dual", MMKV.MULTI_PROCESS_MODE)
    }

    private fun getFlowData(): Boolean {
        val data = mmkv.decodeBool("rl_data", true)
        Log.e("TAG", "getAroundFlowJsonData-ss: ${data}")
        return data
    }

    fun brand(builder:VpnService.Builder, myPackageName: String) {
        if(getFlowData()){
            //黑名单绕流
            (listOf(myPackageName) + listGmsPackages())
                .iterator()
                .forEachRemaining {
                    runCatching { builder.addDisallowedApplication(it) }
                }
        }
    }

    private fun listGmsPackages(): List<String> {
        return listOf(
            "com.google.android.gms",
            "com.google.android.ext.services",
            "com.google.process.gservices",
            "com.android.vending",
            "com.google.android.gms.persistent",
            "com.google.android.cellbroadcastservice",
            "com.google.android.packageinstaller",
            "com.google.android.gms.location.history",
        )
    }
}