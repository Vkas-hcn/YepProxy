package how.to.finish.the.project.tricevpn.hlep

import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.base.App.Companion.mmkvDua


object DataUtils {
    const val TAG = "DualProtocolOne"
    const val pp_url = "https://www.baidu.com"
    const val ip_url = "https://ifconfig.me/ip"
    const val clock_url = "https://visa.easyconnectionprocy.com/cheat/min/purine"
    const val vpn_data_type = "pactyep"
    const val fast_data_type = "stratyem"
    const val ad_data_type = "tionyet"
    const val user_data_type = "tryyrh"
    const val lj_data_type = "xianyer"
    var vpn_list: String = ""
        set(value) {
            mmkvDua.encode("vpn_list", value)
            field = value
        }
        get() = mmkvDua.decodeString("vpn_list", "") ?: ""

    var vpn_fast: String = ""
        set(value) {
            mmkvDua.encode("vpn_fast", value)
            field = value
        }
        get() = mmkvDua.decodeString("vpn_fast", "") ?: ""

    var ad_data: String = ""
        set(value) {
            mmkvDua.encode("ad_data", value)
            field = value
        }
        get() = mmkvDua.decodeString("ad_data", "") ?: ""
    var user_data: String = ""
        set(value) {
            mmkvDua.encode("user_data", value)
            field = value
        }
        get() = mmkvDua.decodeString("user_data", "") ?: ""
    var lj_data: String = ""
        set(value) {
            mmkvDua.encode("lj_data", value)
            field = value
        }
        get() = mmkvDua.decodeString("lj_data", "") ?: ""

    var recently_nums: String = ""
        set(value) {
            mmkvDua.encode("recently_nums", value)
            field = value
        }
        get() = mmkvDua.decodeString("recently_nums", "") ?: ""
    var ip_data: String = ""
        set(value) {
            mmkvDua.encode("ip_data", value)
            field = value
        }
        get() = mmkvDua.decodeString("ip_data", "") ?: ""
    var ip_data2: String = ""
        set(value) {
            mmkvDua.encode("ip_data2", value)
            field = value
        }
        get() = mmkvDua.decodeString("ip_data2", "") ?: ""
    var connect_vpn: String = ""
        set(value) {
            mmkvDua.encode("connect_vpn", value)
            field = value
        }
        get() = mmkvDua.decodeString("connect_vpn", "") ?: ""

    var refer_data: String = ""
        set(value) {
            mmkvDua.encode("refer_data", value)
            field = value
        }
        get() = mmkvDua.decodeString("refer_data", "") ?: ""
    var uu0d_yep: String = ""
        set(value) {
            mmkvDua.encode("uu0d_yep", value)
            field = value
        }
        get() = mmkvDua.decodeString("uu0d_yep", "") ?: ""
    var clock_data
        set(value) {
            mmkvDua.encode("clock_data", value)
        }
        get() = mmkvDua.decodeString("clock_data", "") ?: ""
    var agreement_type:Int = 0
        set(value) {
            mmkvDua.encode("agreement_type", value)
            field = value
        }
        get() = mmkvDua.decodeInt("agreement_type", 0)
    fun String.getServiceFlag(): Int {
        return when (this) {
            "United States" -> R.drawable.us
            "Australia" -> R.drawable.australia
            "Canada" -> R.drawable.canada
            "China" -> R.drawable.china
            "France" -> R.drawable.france
            "Germany" -> R.drawable.de
            "Hong Kong" -> R.drawable.hongkong
            "India" -> R.drawable.india
            "Japan" -> R.drawable.japan
            "koreasouth" -> R.drawable.korea
            "Singapore" -> R.drawable.singapore
            "Taiwan" -> R.drawable.taiwan
            "Brazil" -> R.drawable.brazil
            "Czech Republic" -> R.drawable.czechrepublic
            "United Kingdom" -> R.drawable.gb
            "India" -> R.drawable.india
            "Nether Lands" -> R.drawable.netherlands
            else -> R.drawable.fast
        }
    }

    const val connectionYepStatus="connectionYepStatus"
    const val serverYepInformation="serverYepInformation"
    const val whetherYepConnected="whetherYepConnected"
    const val currentYepService="currentYepService"
}