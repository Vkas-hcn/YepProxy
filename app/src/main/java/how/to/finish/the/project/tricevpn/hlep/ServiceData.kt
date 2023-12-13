package how.to.finish.the.project.tricevpn.hlep

import android.util.Log
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ServiceData {


    val ss_vpn_list = """
        WwogICAgewogICAgICAgICJvbkx1IjogIiBuVENSRGlmeFA0Z2VidzJwRlFONz0iLAogICAgICAgICJvbkxpIjogImFlcy0yNTYtZ2NtIiwKICAgICAgICAib25MbyI6ICIzMzExIiwKICAgICAgICAib25McCI6ICJVbml0ZWQgU3RhdGVzIiwKICAgICAgICAib25MbCI6ICJTZWF0dGxlIiwKICAgICAgICAib25MbSI6ICI2Ni40Mi42NC41NSIKICAgIH0sCiAgICB7CiAgICAgICAgIm9uTHUiOiAiIG5UQ1JEaWZ4UDRnZWJ3MnBGUU43PSIsCiAgICAgICAgIm9uTGkiOiAiYWVzLTI1Ni1nY20iLAogICAgICAgICJvbkxvIjogIjMzMTIiLAogICAgICAgICJvbkxwIjogIkphcGFuIiwKICAgICAgICAib25MbCI6ICJUb2t5byIsCiAgICAgICAgIm9uTG0iOiAiNDUuNzYuMjE0Ljk0IgogICAgfSwKICAgIHsKICAgICAgICAib25MdSI6ICIgblRDUkRpZnhQNGdlYncycEZRTjc9IiwKICAgICAgICAib25MaSI6ICJhZXMtMjU2LWdjbSIsCiAgICAgICAgIm9uTG8iOiAiMzMxMyIsCiAgICAgICAgIm9uTHAiOiAiVW5pdGVka2luZ2RvbSIsCiAgICAgICAgIm9uTGwiOiAiTG9uZG9uIiwKICAgICAgICAib25MbSI6ICI5NS4xNzkuMTk2LjEzMCIKICAgIH0sCiAgICB7CiAgICAgICAgIm9uTHUiOiAiIG5UQ1JEaWZ4UDRnZWJ3MnBGUU43PSIsCiAgICAgICAgIm9uTGkiOiAiYWVzLTI1Ni1nY20iLAogICAgICAgICJvbkxvIjogIjMzMTQiLAogICAgICAgICJvbkxwIjogIkNhbmFkYSIsCiAgICAgICAgIm9uTGwiOiAiVG9yb250byIsCiAgICAgICAgIm9uTG0iOiAiMTU1LjEzOC4xMzguMTM3ICIKICAgIH0KXQ==
    """.trimIndent()

    val vpn_fast = """
        WwoiNjYuNDIuNjQuNTUiCl0=
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
}