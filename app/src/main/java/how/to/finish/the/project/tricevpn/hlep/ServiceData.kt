package how.to.finish.the.project.tricevpn.hlep

import android.util.Log
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    "lieayer": "1",
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
    fun    getVpnList(): MutableList<ServiceBean> {
        val jsonString= DataUtils.vpn_list.ifEmpty {
            decodeBase64(ss_vpn_list)
        }

        return try {
            Gson().fromJson(jsonString, object : TypeToken<ArrayList<ServiceBean>>() {}.type)
        } catch (e: Exception) {
            e.printStackTrace()
            Gson().fromJson(decodeBase64(ss_vpn_list), object : TypeToken<ArrayList<ServiceBean>>() {}.type)
        }
    }


    //找出fast服务器
    fun getFastVpn(): ServiceBean {
        val jsonString= DataUtils.vpn_fast.ifEmpty {
            decodeBase64(vpn_fast)
        }

        val fast = try {
            Gson().fromJson<ArrayList<String>>(jsonString, object : TypeToken<ArrayList<String>>() {}.type)
        } catch (e: Exception) {
            e.printStackTrace()
            Gson().fromJson(decodeBase64(vpn_fast), object : TypeToken<ArrayList<String>>() {}.type)
        }
        var bean = ServiceBean("","","","","","",
            best = true,
            smart = true,
            check = false
        )
        getVpnList().forEach {
            if (fast.getOrNull(0) == it.ip) {
                bean = it
                bean.best = true
                bean.smart = true
                bean.country = "Fast Server"
            }
        }
        return bean
    }

    fun getAllVpnListData(): MutableList<ServiceBean>{
        val list = mutableListOf<ServiceBean>()
        list.add(getFastVpn())
        list.addAll(getVpnList())
        return list
    }

    fun getRecentlyList(): MutableList<String> {
        val list = mutableListOf<String>()
        DataUtils.recently_nums.split(",").forEach {
            if (it.isNotEmpty() && list.contains(it).not()) {
                list.add(it)
            }
        }
        Log.e(TAG, "getHistoryList: ${list}", )
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
    fun getBase64String(data:String): String {
        return String(android.util.Base64.decode(data, android.util.Base64.DEFAULT))
    }
    fun fromJson(json: String): YepAdBean {
        val gson = Gson()
        return gson.fromJson(json, YepAdBean::class.java)
    }

}