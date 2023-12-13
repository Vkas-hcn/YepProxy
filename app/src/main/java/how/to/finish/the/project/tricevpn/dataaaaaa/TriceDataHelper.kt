package how.to.finish.the.project.tricevpn.dataaaaaa
import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.Keep
import com.google.android.gms.common.util.Base64Utils
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import how.to.finish.the.project.tricevpn.BuildConfig
import how.to.finish.the.project.tricevpn.baseeeeee.BaseApp
import how.to.finish.the.project.tricevpn.uitlllll.SPUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Type

object TriceDataHelper {
    var curPosition: Int
        get() {
            var node = SPUtil(BaseApp.getInstance().applicationContext).getInt(
                "curPosition",
                0
            )
            if (allLocaleProfiles.size <= node) node = 0
            return node
        }
        set(value) {
            SPUtil(BaseApp.getInstance().applicationContext).putInt(
                "curPosition",
                value
            )
        }

    var historyPosList: String
        get() {
            val pos = SPUtil(BaseApp.getInstance().applicationContext).getString(
                "historyPosList",
                ""
            )
            return pos
        }
        set(value) {
            SPUtil(BaseApp.getInstance().applicationContext).putString(
                "historyPosList",
                value
            )
        }
    fun getHistoryList(): MutableList<String> {
        val list = mutableListOf<String>()
        historyPosList.split(",").forEach {
            if (it.isNotEmpty() && list.contains(it).not()) {
                list.add(it)
            }
        }
        Log.e("trice", "getHistoryList: ${list}", )
        return list.subList(0, list.size.coerceAtMost(3))
    }

    //存储书签数据到本地
    fun saveHistoryList(pos: String) {
        val data = "$pos,$historyPosList"
        historyPosList = data
    }

    var cachePosition = -1

    val allLocaleProfiles: ArrayList<SunProfile> by lazy {
        getAllLocaleProfile()
    }

    private fun getAllLocaleProfile(): ArrayList<SunProfile> {
        if ((remoteAllList?.size ?: 0) > 0) {
            val dataList = remoteAllList!!
            return if ((remoteSmartStringList?.size ?: 0) > 0) {
                val a: SunProfile? = dataList.findLast {
//                    remoteVPNSmartString?.take(3)?.random().toString() == it.onLm_host
                    smartListS?.get(0).toString() == it.iphostttt
                }
                if (a != null)
                    dataList.apply { this.add(a) }
                else
                    dataList.apply {
                        add(0, getSmart())
                    }
            } else {
                dataList.apply {
                    add(0, getSmart())
                }
            }
        } else
            return getJsonProfile().apply {
                add(0, getSmart())
            }
    }

    fun getSmart(): SunProfile {
        val gson = Gson()
        val data = gson.fromJson(String(Base64Utils.decode(jsonSmart)), SunProfile::class.java)
        return data.apply { this.isFast = true }
    }


    private var jsonLocaleData = """
      WwogICAgewogICAgICAgICJnZ19hX2EiOiAib3g4S2tzbVFYeW5FbVZFZE9DcHciLAogICAgICAgICJnZ19hX3kiOiAiQUVTLTI1Ni1HQ00iLAogICAgICAgICJnZ19hX2IiOiAiNDQzIiwKICAgICAgICAiZ2dfYV9wIjogIlVuaXRlZCBTdGF0ZXMiLAogICAgICAgICJnZ19hX2MiOiAiTWlhbWkiLAogICAgICAgICJnZ19hX28iOiAiMTAzLjkwLjE2MC4xMjkiCiAgICB9LAogICAgewogICAgICAgICJnZ19hX2EiOiAib3g4S2tzbVFYeW5FbVZFZE9DcHciLAogICAgICAgICJnZ19hX3kiOiAiQUVTLTI1Ni1HQ00iLAogICAgICAgICJnZ19hX2IiOiAiNDQzIiwKICAgICAgICAiZ2dfYV9wIjogImdlcm1hbnkiLAogICAgICAgICJnZ19hX2MiOiAiRnJhbmtmdXJ0IiwKICAgICAgICAiZ2dfYV9vIjogIjEzNi4yNDQuOTQuMjUzIgogICAgfSwKICAgIHsKICAgICAgICAiZ2dfYV9hIjogIm94OEtrc21RWHluRW1WRWRPQ3B3IiwKICAgICAgICAiZ2dfYV95IjogIkFFUy0yNTYtR0NNIiwKICAgICAgICAiZ2dfYV9iIjogIjQ0MyIsCiAgICAgICAgImdnX2FfcCI6ICJrb3JlYXNvdXRoIiwKICAgICAgICAiZ2dfYV9jIjogIlNlb3VsIiwKICAgICAgICAiZ2dfYV9vIjogIjE1OC4yNDcuMTkzLjE4NiIKICAgIH0sCiAgICB7CiAgICAgICAgImdnX2FfYSI6ICJveDhLa3NtUVh5bkVtVkVkT0NwdyIsCiAgICAgICAgImdnX2FfeSI6ICJBRVMtMjU2LUdDTSIsCiAgICAgICAgImdnX2FfYiI6ICI0NDMiLAogICAgICAgICJnZ19hX3AiOiAic3dpdHplcmxhbmQiLAogICAgICAgICJnZ19hX2MiOiAiWnVyaWNoIiwKICAgICAgICAiZ2dfYV9vIjogIjE3OC4yMDkuNDYuMTUwIgogICAgfQpd
    """.trimIndent()

//    [
//    {
//        "gg_a_a": "ox8KksmQXynEmVEdOCpw",
//        "gg_a_y": "AES-256-GCM",
//        "gg_a_b": "443",
//        "gg_a_p": "United States",
//        "gg_a_c": "Miami",
//        "gg_a_o": "103.90.160.129"
//    },
//    {
//        "gg_a_a": "ox8KksmQXynEmVEdOCpw",
//        "gg_a_y": "AES-256-GCM",
//        "gg_a_b": "443",
//        "gg_a_p": "germany",
//        "gg_a_c": "Frankfurt",
//        "gg_a_o": "136.244.94.253"
//    },
//    {
//        "gg_a_a": "ox8KksmQXynEmVEdOCpw",
//        "gg_a_y": "AES-256-GCM",
//        "gg_a_b": "443",
//        "gg_a_p": "koreasouth",
//        "gg_a_c": "Seoul",
//        "gg_a_o": "158.247.193.186"
//    },
//    {
//        "gg_a_a": "ox8KksmQXynEmVEdOCpw",
//        "gg_a_y": "AES-256-GCM",
//        "gg_a_b": "443",
//        "gg_a_p": "switzerland",
//        "gg_a_c": "Zurich",
//        "gg_a_o": "178.209.46.150"
//    }
//    ]

    private var jsonSmart = """
        ewogICAgICAgICJnZ19hX2EiOiAib3g4S2tzbVFYeW5FbVZFZE9DcHciLAogICAgICAgICJnZ19hX3kiOiAiQUVTLTI1Ni1HQ00iLAogICAgICAgICJnZ19hX2IiOiAiNDQzIiwKICAgICAgICAiZ2dfYV9wIjogIlVuaXRlZCBTdGF0ZXMiLAogICAgICAgICJnZ19hX2MiOiAiTWlhbWkiLAogICAgICAgICJnZ19hX28iOiAiMTAzLjkwLjE2MC4xMjkiCiAgICB9
        """.trimIndent()


//    {
//        "gg_a_a": "ox8KksmQXynEmVEdOCpw",
//        "gg_a_y": "AES-256-GCM",
//        "gg_a_b": "443",
//        "gg_a_p": "United States",
//        "gg_a_c": "Miami",
//        "gg_a_o": "103.90.160.129"
//    }
    private fun getJsonProfile(): ArrayList<SunProfile> {
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<SunProfile>>() {}.type
        return gson.fromJson(String(Base64Utils.decode(jsonLocaleData)), listType)
    }


    private var allPListS: String? = null
    private var smartListS: String? = null

    @SuppressLint("StaticFieldLeak")
    var remoteConfig: FirebaseRemoteConfig? = null
    private var isGetRemoteString = false

    private var remoteAllList: ArrayList<SunProfile>? = null
    private var remoteSmartStringList: ArrayList<String>? = null


    private fun appInitGetVPNFB() {
        remoteConfig = Firebase.remoteConfig
        remoteConfig?.fetchAndActivate()?.addOnSuccessListener {
            allPListS = remoteConfig?.getString("gg_a_mm")
            smartListS = remoteConfig?.getString("gg_a_tt")
            dealFBData()
        }
    }

    private fun dealFBData() {
        if (allPListS?.isNotBlank() == true) {
            try {
                val data = allPListS
                val gson = Gson()
                val base64ListAd = data?.let { Base64Utils.decode(it) }
                val listType: Type = object : TypeToken<ArrayList<SunProfile>>() {}.type
                val dataList: ArrayList<SunProfile> =
                    gson.fromJson(base64ListAd.toString(), listType)
                if (dataList.size > 0) {
                    isGetRemoteString = true
                    remoteAllList = dataList
                }
            } catch (e: Exception) {
                e.printStackTrace()
                allPListS = null
            }
        }
        if (smartListS?.isNotBlank() == true) {
            try {
                val data = smartListS
                val gson = Gson()
                val base64ListAd = data?.let { Base64Utils.decode(it) }
                val listType: Type = object : TypeToken<ArrayList<String>>() {}.type
                val dataList: ArrayList<String> = gson.fromJson(base64ListAd.toString(), listType)
                if (dataList.size > 0) {
                    remoteSmartStringList = dataList
                }
            } catch (e: Exception) {
                e.printStackTrace()
                smartListS = null
            }
        }

    }


    fun initVpnFb() {
        if (!BuildConfig.DEBUG) {
            appInitGetVPNFB()
            MainScope().launch {
                delay(4000)
                if (!isGetRemoteString) {
                    while (true) {
                        if (!isGetRemoteString) dealFBData()
                        delay(2000)
                    }
                }
            }
        }
    }

}

@Keep
data class SunProfile(
    @SerializedName("gg_a_a")
    var ppppppppass: String = "",
    @SerializedName("gg_a_y")
    var polyyyyyyy: String = "",
    @SerializedName("gg_a_b")
    var ppppppppport: Int = 0,//port
    @SerializedName("gg_a_p")
    var counnnn: String = "",//name
    @SerializedName("gg_a_c")
    var ccccccci: String = "",//city
    @SerializedName("gg_a_o")//ip_host
    var iphostttt: String = "",
    var isFast: Boolean = false
)
