package how.to.finish.the.project.tricevpn.hlep

import android.util.Log
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ServiceData {
    //本地买量数据
    const val local_yep_data = """
        {
    "onLleav": 1,
    "onLeate": 2,
    "onLmill": 2,
    "onLage": 2,
    "onLiden": 2,
    "onLclem": 2,
    "onLisp": 2
}
    """
    //本地广告逻辑
    const val local_yep_logic = """
{
    "onLmatt": "2",
    "onLprob": "2",
    "onLfeli": "2"
}    """
    val local_ad_data = """
{
  "open":"ca-app-pub-3940256099942544/9257395921",
  "home":"ca-app-pub-3940256099942544/2247696110",
  "end":"ca-app-pub-3940256099942544/2247696110",
  "connect":"ca-app-pub-3940256099942544/8691691433",
  "back":"ca-app-pub-3940256099942544/1033173712"
}
    """.trimIndent()

    val ss_vpn_list = """
        WwogICAgewogICAgICAgICJnZ19hX2EiOiAiajl4S0RKZDcxdXJFUGJneWxjNjJCWkgiLAogICAgICAgICJnZ19hX3kiOiAiY2hhY2hhMjAtaWV0Zi1wb2x5MTMwNSIsCiAgICAgICAgImdnX2FfYiI6ICI5NjQxIiwKICAgICAgICAiZ2dfYV9wIjogIlVuaXRlZCBTdGF0ZXMiLAogICAgICAgICJnZ19hX2MiOiAiTWlhbWkiLAogICAgICAgICJnZ19hX28iOiAiNDMuMjMxLjIzNC44MSIKICAgIH0sCiAgICB7CiAgICAgICAgImdnX2FfYSI6ICJqOXhLREpkNzF1ckVQYmd5bGM2MkJaSCIsCiAgICAgICAgImdnX2FfeSI6ICJjaGFjaGEyMC1pZXRmLXBvbHkxMzA1IiwKICAgICAgICAiZ2dfYV9iIjogIjk2NDIiLAogICAgICAgICJnZ19hX3AiOiAiR2VybWFueSIsCiAgICAgICAgImdnX2FfYyI6ICJGcmFua2Z1cnQiLAogICAgICAgICJnZ19hX28iOiAiNDMuMjMxLjIzNC44MiIKICAgIH0sCiAgICB7CiAgICAgICAgImdnX2FfYSI6ICJqOXhLREpkNzF1ckVQYmd5bGM2MkJaSCIsCiAgICAgICAgImdnX2FfeSI6ICJjaGFjaGEyMC1pZXRmLXBvbHkxMzA1IiwKICAgICAgICAiZ2dfYV9iIjogIjk2NDMiLAogICAgICAgICJnZ19hX3AiOiAia29yZWFzb3V0aCIsCiAgICAgICAgImdnX2FfYyI6ICJTZW91bCIsCiAgICAgICAgImdnX2FfbyI6ICI0My4yMzEuMjM0LjgzIgogICAgfSwKICAgIHsKICAgICAgICAiZ2dfYV9hIjogImo5eEtESmQ3MXVyRVBiZ3lsYzYyQlpIIiwKICAgICAgICAiZ2dfYV95IjogImNoYWNoYTIwLWlldGYtcG9seTEzMDUiLAogICAgICAgICJnZ19hX2IiOiAiOTY0NCIsCiAgICAgICAgImdnX2FfcCI6ICJTaW5nYXBvcmUiLAogICAgICAgICJnZ19hX2MiOiAiU2luZ2Fwb3JlIiwKICAgICAgICAiZ2dfYV9vIjogIjQzLjIzMS4yMzQuODQiCiAgICB9Cl0=
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