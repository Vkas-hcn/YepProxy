package com.yep.online.link.prox.connection.net

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.installreferrer.api.ReferrerDetails
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.ResponseInfo
import com.google.gson.Gson
import com.yep.online.link.prox.connection.hlep.DataUtils
import com.yep.online.link.prox.connection.hlep.DataUtils.TAG
import com.yep.online.link.prox.connection.hlep.YepAdBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class YepOkHttpUtils {
    val client = OkHttpClientUtils()

    fun getTbaIp(context: Context) {
        try {
            client.get(context, "https://ifconfig.me/ip", object : OkHttpClientUtils.Callback {
                override fun onSuccess(response: String) {
                    Log.e(TAG, "IP----->${response}")
                    DataUtils.ip_tab = response
                }

                override fun onFailure(error: String) {
                }
            })
        } catch (e: Exception) {

        }

    }

    fun getCurrentIp(context: Context) {
        getCurrentIp2(context)
        try {
            client.get(context, DataUtils.ip_url, object : OkHttpClientUtils.Callback {
                override fun onSuccess(response: String) {
                    Log.e(TAG, "IP----->${response}")
                    DataUtils.ip_data = response
                }

                override fun onFailure(error: String) {
                }
            })
        } catch (e: Exception) {

        }

    }

    fun getCurrentIp2(context: Context) {
        try {
            client.get(context, "https://api.myip.com/", object : OkHttpClientUtils.Callback {
                override fun onSuccess(response: String) {
                    Log.e(TAG, "IP2----->${response}")
                    DataUtils.ip_data2 = response
                }

                override fun onFailure(error: String) {
                }
            })
        } catch (e: Exception) {

        }

    }

    //发起黑名单请求
    fun getBlackList(activity: AppCompatActivity) {
        val map = CloakUtils.cloakJson(activity)
        Log.e("TAG", "Blacklist request data= $map")
        try {
            client.getMap(DataUtils.clock_url, map, object : OkHttpClientUtils.Callback {
                override fun onSuccess(response: String) {
                    Log.e(TAG, "clock_data----->${response}")
                    DataUtils.clock_data = response
                }

                override fun onFailure(error: String) {
                    retry(activity)
                }
            })
        } catch (e: Exception) {
            retry(activity)
        }
    }

    fun retry(activity: AppCompatActivity) {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            delay(10000)
            getBlackList(activity)
        }
    }

    fun getSessionList(context: Context) {
        val data = CloakUtils.getSessionJson(context)
        Log.e(TAG, "Session request data= $data")
        try {
            client.post(DataUtils.tab_url, data, object : OkHttpClientUtils.Callback {
                override fun onSuccess(response: String) {
                    Log.e(TAG, "Session-- success----->${response}")
                    DataUtils.clock_data = response
                }

                override fun onFailure(error: String) {
                    Log.e(TAG, "Session-- error----->${error}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Session-- error----->${e}")
        }
    }

    fun getInstallList(context: Context, rd: ReferrerDetails) {
        val data = CloakUtils.getInstallJson(rd, context)
        Log.e(TAG, "Install request data= $data")
        try {
            client.post(DataUtils.tab_url, data, object : OkHttpClientUtils.Callback {
                override fun onSuccess(response: String) {
                    Log.e(TAG, "Install 事件上报成功----->${response}")
                    DataUtils.refer_tab = true
                }

                override fun onFailure(error: String) {
                    DataUtils.refer_tab = false
                    Log.e(TAG, "Install事件上报失败----->${error}")

                }
            })
        } catch (e: Exception) {
            DataUtils.refer_tab = false
            Log.e(TAG, "Install事件上报失败----->${e}")

        }
    }

    fun getAdList(
        context: Context,
        adValue: AdValue,
        responseInfo: ResponseInfo,
        type: String,
        yepAdBean: YepAdBean
    ) {
        val json = CloakUtils.getAdJson(context, adValue, responseInfo, type, yepAdBean)
        Log.e(TAG, "ad---${type}--request data-->${json}")
        try {
            client.post(DataUtils.tab_url, json, object : OkHttpClientUtils.Callback {
                override fun onSuccess(response: String) {
                    Log.e(TAG, "${type}广告事件上报-成功->")
                }

                override fun onFailure(error: String) {
                    Log.e(TAG, "${type}广告事件上报-失败-->${error}")
                }
            })
        } catch (e: Exception) {

        }
    }


    fun getTbaList(
        context: Context,
        eventName: String,
        parameterName: String = "",
        tbaValue: Any = 0,
        wTime: Int = 0,
    ) {
        val json = if (wTime == 0) {
            CloakUtils.getTbaDataJson(context, eventName)
        } else {
            CloakUtils.getTbaTimeDataJson(context, tbaValue, eventName, parameterName)
        }
        Log.e(TAG, "${eventName}--TBA事件上报-->${json}")
        try {
            client.post(DataUtils.tab_url, json, object : OkHttpClientUtils.Callback {
                override fun onSuccess(response: String) {
                    Log.e(TAG, "${eventName}--TBA事件上报-成功->")
                }

                override fun onFailure(error: String) {
                    Log.e(TAG, "${eventName}--TBA事件上报-失败-->${error}")
                }
            })
        } catch (e: Exception) {

        }
    }


    fun getVpnData(context: Context) {
        CloakUtils.putPointYep("ye_qq", context)
        val date = System.currentTimeMillis()
        try {
            client.get(context, DataUtils.vpn_url, object : OkHttpClientUtils.Callback {
                override fun onSuccess(response: String) {
                    val responseData = processResponse(response)
                    DataUtils.vpn_online = responseData
                    Timber.tag(TAG).e("获取下发服务器数据-成功->" + DataUtils.vpn_online)
                    CloakUtils.putPointYep("ye_hq", context)
                    val date2 = (System.currentTimeMillis()-date)/1000
                    CloakUtils.putPointTimeYep("ye_tm", date2,"time",context)
                }

                override fun onFailure(error: String) {
                    Log.e(TAG, "获取下发服务器数据-失败->${error}")
                }
            })
        } catch (e: Exception) {
        }
    }


    fun processResponse(response: String): String {
        // 去掉尾部34个字符
        val trimmedResponse = response.dropLast(34)

        // 大小写互换
        val swappedCaseResponse = trimmedResponse.map {
            when {
                it.isUpperCase() -> it.toLowerCase()
                it.isLowerCase() -> it.toUpperCase()
                else -> it
            }
        }.joinToString("")

        // 使用 Base64 解码
        val decodedBytes = Base64.decode(swappedCaseResponse, Base64.DEFAULT)
        val decodedString = String(decodedBytes, Charsets.UTF_8)

        return decodedString
    }
}