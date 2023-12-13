package how.to.finish.the.project.tricevpn.hlep

import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.base.App.Companion.mmkvDua


object DataUtils {
    const val TAG = "DualProtocolOne"
    const val pp_url = "https://www.baidu.com"

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

    var recently_nums: String = ""
        set(value) {
            mmkvDua.encode("recently_nums", value)
            field = value
        }
        get() = mmkvDua.decodeString("recently_nums", "") ?: ""

    var connect_vpn_pos: Int = 0
        set(value) {
            mmkvDua.encode("connect_vpn_pos", value)
            field = value
        }
        get() = mmkvDua.decodeInt("connect_vpn_pos", 0)
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
            "Korea" -> R.drawable.korea
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
}