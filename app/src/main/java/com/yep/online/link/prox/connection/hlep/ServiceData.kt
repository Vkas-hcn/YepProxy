package com.yep.online.link.prox.connection.hlep

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yep.online.link.prox.connection.net.YepOkHttpUtils

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
    "coronyer": "3",
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

    val ss_vpn_list = """
        WwogICAgewogICAgICAgICJ3ZXJ5ZXAiOiAiajl4S0RKZDcxdXJFUGJneWxjNjJCWkgiLAogICAgICAgICJ1cmV5ZXAiOiAiY2hhY2hhMjAtaWV0Zi1wb2x5MTMwNSIsCiAgICAgICAgImFycnllcCI6ICI5NjQxIiwKICAgICAgICAidmVyeXllcCI6ICJVbml0ZWQgU3RhdGVzIiwKICAgICAgICAic2NpZW50eWVwIjogIk1pYW1pIiwKICAgICAgICAic3RmdWx5ZXAiOiAiNDMuMjMxLjIzNC44MSIKICAgIH0sCiAgICB7CiAgICAgICAgIndlcnllcCI6ICJqOXhLREpkNzF1ckVQYmd5bGM2MkJaSCIsCiAgICAgICAgInVyZXllcCI6ICJjaGFjaGEyMC1pZXRmLXBvbHkxMzA1IiwKICAgICAgICAiYXJyeWVwIjogIjk2NDEiLAogICAgICAgICJ2ZXJ5eWVwIjogIkdlcm1hbnkiLAogICAgICAgICJzY2llbnR5ZXAiOiAiTG9uZG9uIiwKICAgICAgICAic3RmdWx5ZXAiOiAiMTg1LjUzLjIxMS4xNjkiCiAgICB9LAogICAgewogICAgICAgICJ3ZXJ5ZXAiOiAiajl4S0RKZDcxdXJFUGJneWxjNjJCWkgiLAogICAgICAgICJ1cmV5ZXAiOiAiY2hhY2hhMjAtaWV0Zi1wb2x5MTMwNSIsCiAgICAgICAgImFycnllcCI6ICI5NjQzIiwKICAgICAgICAidmVyeXllcCI6ICJrb3JlYXNvdXRoIiwKICAgICAgICAic2NpZW50eWVwIjogIlNlb3VsIiwKICAgICAgICAic3RmdWx5ZXAiOiAiNDMuMjMxLjIzNC44MyIKICAgIH0sCiAgICB7CiAgICAgICAgIndlcnllcCI6ICJqOXhLREpkNzF1ckVQYmd5bGM2MkJaSCIsCiAgICAgICAgInVyZXllcCI6ICJjaGFjaGEyMC1pZXRmLXBvbHkxMzA1IiwKICAgICAgICAiYXJyeWVwIjogIjk2NDQiLAogICAgICAgICJ2ZXJ5eWVwIjogIlNpbmdhcG9yZSIsCiAgICAgICAgInNjaWVudHllcCI6ICJTaW5nYXBvcmUiLAogICAgICAgICJzdGZ1bHllcCI6ICI0My4yMzEuMjM0Ljg0IgogICAgfQpd
    """.trimIndent()

    val vpn_fast = """
        WwoiNDMuMjMxLjIzNC44MSIKXQ==
    """.trimIndent()


    //base64解密
    fun decodeBase64(str: String): String {
        return String(android.util.Base64.decode(str, android.util.Base64.DEFAULT))
    }


    //解析vpn列表
    fun getVpnList(): MutableList<ServiceBean> {
        val jsonString = DataUtils.vpn_list.ifEmpty {
            decodeBase64(ss_vpn_list)
        }
        return try {
            Gson().fromJson(jsonString, object : TypeToken<ArrayList<ServiceBean>>() {}.type)
        } catch (e: Exception) {
            e.printStackTrace()
            Gson().fromJson(
                decodeBase64(ss_vpn_list),
                object : TypeToken<ArrayList<ServiceBean>>() {}.type
            )
        }
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
        list?.add(getFastVpnOnLine())
        list?.addAll(getVpnList())
        return list ?: getVpnList()
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
//    fun getFastVpn(): ServiceBean {
//        val jsonString= DataUtils.vpn_fast.ifEmpty {
//            decodeBase64(vpn_fast)
//        }
//
//        val fast = try {
//            Gson().fromJson<ArrayList<String>>(jsonString, object : TypeToken<ArrayList<String>>() {}.type)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Gson().fromJson(decodeBase64(vpn_fast), object : TypeToken<ArrayList<String>>() {}.type)
//        }
//        var bean = ServiceBean("","","","","","",
//            best = true,
//            smart = true,
//            check = false
//        )
//        getVpnList().forEach {
//            if (fast.getOrNull(0) == it.ip) {
//                bean = it
//                bean.best = true
//                bean.smart = true
//                bean.country = "Fast Server"
//            }
//        }
//        return bean
//    }

    fun getFastVpnOnLine(): ServiceBean {
        val ufVpnBean: MutableList<ServiceBean> = getDataFastServerData() ?: local
        return ufVpnBean.shuffled().first().apply {
            best = true
            smart = true
            country = "Fast Server"
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
            if (spinVpnBean?.data?.fdbTNoDnP?.isNotEmpty() == true) {
                spinVpnBean.data.fdbTNoDnP.map {
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
            if (spinVpnBean?.data?.tvxF?.isNotEmpty() == true) {
                spinVpnBean.data.tvxF.map {
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