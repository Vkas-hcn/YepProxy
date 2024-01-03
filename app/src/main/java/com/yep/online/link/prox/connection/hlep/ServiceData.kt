package com.yep.online.link.prox.connection.hlep

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yep.online.link.prox.connection.hlep.DataUtils.TAG
import com.yep.online.link.prox.connection.net.YepOkHttpUtils
import timber.log.Timber

object ServiceData {
    //本地买量数据
    const val local_yep_data = """
        {
    "dineyrh": 2,
    "tempyrh": 2,
    "calyrh": 2,
    "hisyrh": 1,
    "pteryrh": 2,
    "oeeryrh": 2,
    "adoryrh": 2
}
    """

    //本地广告逻辑
    const val local_yep_logic = """
{
    "coronyer": "1",
    "lieayer": "2",
    "toryyer": "2"
}    """
    val local_ad_data = """
{
  "ousyet":"ca-app-pub-3940256099942544/9257395921",
  "bedyet":"ca-app-pub-3940256099942544/2247696110",
  "queyet":"ca-app-pub-3940256099942544/2247696110",
  "tranyet":"ca-app-pub-3940256099942544/8691691433",
  "furtyet":"ca-app-pub-3940256099942544/1033173712"
}
    """.trimIndent()


    //base64解密
    fun decodeBase64(str: String): String {
        return String(android.util.Base64.decode(str, android.util.Base64.DEFAULT))
    }


    fun getRecentlyList(): MutableList<String> {
        val list = mutableListOf<String>()
        DataUtils.recently_nums.split(",").forEach {
            if (it.isNotEmpty() && list.contains(it).not()) {
                list.add(it)
            }
        }
        return list.subList(0, list.size.coerceAtMost(3))
    }

    fun saveRecentlyList(pos: String) {
        val data = "$pos,${DataUtils.recently_nums}"
        DataUtils.recently_nums = data
    }

    fun findVpnByPos(): MutableList<ServiceBean> {
        val dataList = mutableListOf<ServiceBean>()
        getRecentlyList().forEach {
            dataList.add(getAllVpnListData()[it.toInt()])
        }
        return dataList
    }

    fun getAdJson(): YepAdBean {
        val dataJson = DataUtils.ad_data.let {
            if (it.isNullOrEmpty()) {
                local_ad_data
            } else {
                getBase64String(it)
            }
        }
        return runCatching {
            fromJson(dataJson)
        }.getOrNull() ?: fromJson(local_ad_data)
    }

    fun getUserJson(): YepUserBean {
        val dataJson = DataUtils.user_data.let {
            if (it.isNullOrEmpty()) {
                local_yep_data

            } else {
                getBase64String(it)
            }
        }
        return runCatching {
            Gson().fromJson(dataJson, YepUserBean::class.java)
        }.getOrNull() ?: Gson().fromJson(local_yep_data, YepUserBean::class.java)
    }

    fun getLogicJson(): YepLogicBean {
        val dataJson = DataUtils.lj_data.let {
            if (it.isNullOrEmpty()) {
                local_yep_logic

            } else {
                getBase64String(it)
            }
        }
        return runCatching {
            Gson().fromJson(dataJson, YepLogicBean::class.java)
        }.getOrNull() ?: Gson().fromJson(local_yep_logic, YepLogicBean::class.java)
    }

    fun getBase64String(data: String): String {
        return String(android.util.Base64.decode(data, android.util.Base64.DEFAULT))
    }

    fun fromJson(json: String): YepAdBean {
        val gson = Gson()
        return gson.fromJson(json, YepAdBean::class.java)
    }

    fun getAllVpnListData(): MutableList<ServiceBean> {
        val list = getDataFromTheServer()
        val data = Gson().toJson(list)
        Log.e(TAG, "getAllVpnListData: ${data}", )
        list?.add(0, getFastVpnOnLine())
        return list ?: local
    }

    private val local = listOf(
        ServiceBean(
            city = "",
            country = "",
            ip = "",
            agreement = "",
            port = "",
            password = "",
            check = false,
            best = false
        )
    ).toMutableList()

    fun getFastVpnOnLine(): ServiceBean {
        val ufVpnBean: MutableList<ServiceBean>? = getDataFastServerData()
        if (ufVpnBean == null) {
            val data = getDataFromTheServer()?.getOrNull(0)
            return ServiceBean(
                city = data?.city.toString(),
                country = data?.country.toString(),
                ip = data?.ip.toString(),
                agreement = data?.agreement.toString(),
                port = data?.port.toString(),
                password = data?.password.toString(),
                check = false,
                best = true
            )
        } else {
            return ufVpnBean.shuffled().first().apply {
                best = true
                smart = true
                country = "Fast Server"
            }
        }
    }

    /**
     * 下发服务器转换
     */
    fun deliverServerTransitions(context: Context): Boolean {
        val data = getDataFromTheServer()
        return if (data == null) {
            YepOkHttpUtils().getVpnData(context)
            false
        } else {
            true
        }
    }

    /**
     * 获取下发服务器数据
     */
    fun getDataFromTheServer(): MutableList<ServiceBean>? {
        val data = DataUtils.vpn_online
        return runCatching {
            val spinVpnBean = Gson().fromJson(data, OnlineVpnBean::class.java)
            val data = spinVpnBean.data.fdbTNoDnP
            val data2 = data.distinctBy { it.FpEEFA }
            Timber.tag(TAG).e("data=${data}")
            if (data2.isNotEmpty()) {
                data2.map {
                    ServiceBean().apply {
                        ip = it.FpEEFA
                        best = false
                        port = it.gDK.toString()
                        agreement = it.hnek
                        password = it.zbQy
                        city = it.CkqNJTfzhs
                        country = it.PaJsPE
                    }
                }.toMutableList()
            } else {
                null
            }
        }.getOrElse {
            null
        }
    }

    fun getDataFastServerData(): MutableList<ServiceBean>? {
        val data = DataUtils.vpn_online
        return runCatching {
            val spinVpnBean = Gson().fromJson(data, OnlineVpnBean::class.java)
            val data = spinVpnBean.data.tvxF
            val data2 = data.distinctBy { it.FpEEFA }
            if (data.isNotEmpty()) {
                data2.distinctBy { it.FpEEFA }.map {
                    ServiceBean().apply {
                        ip = it.FpEEFA
                        best = false
                        smart = true
                        port = it.gDK.toString()
                        agreement = it.hnek
                        password = it.zbQy
                        city = it.CkqNJTfzhs
                        country = it.PaJsPE
                    }
                }.toMutableList()
            } else {
                null
            }
        }.getOrElse {
            null
        }
    }
}