package how.to.finish.the.project.tricevpn.uitlllll

import how.to.finish.the.project.tricevpn.R

object ImageUtils {
    fun getImage(name: String): Int {
        val a = name.trim().replace(" ", "").lowercase()
        when (a) {
            "belgium" -> return R.mipmap.trice_vpn_belgium
            "brazil" -> return R.mipmap.trice_vpn_brazil
            "canada" -> return R.mipmap.trice_vpn_canada
            "france" -> return R.mipmap.trice_vpn_france
            "germany" -> return R.mipmap.trice_vpn_germany
            "india" -> return R.mipmap.trice_vpn_india
            "ireland" -> return R.mipmap.trice_vpn_ireland
            "italy" -> return R.mipmap.trice_vpn_italy
            "japan" -> return R.mipmap.trice_vpn_japan
            "koreasouth" -> return R.mipmap.trice_vpn_koreasouth
            "netherlands" -> return R.mipmap.trice_vpn_netherlands
            "newzealand" -> return R.mipmap.trice_vpn_newzealand
            "norway" -> return R.mipmap.trice_vpn_norway
            "russianfederation" -> return R.mipmap.trice_vpn_russianfederation
            "singapore" -> return R.mipmap.trice_vpn_singapore
            "sweden" -> return R.mipmap.trice_vpn_sweden
            "switzerland" -> return R.mipmap.trice_vpn_switzerland
            "unitedarabemirates" -> return R.mipmap.trice_vpn_unitedarabemirates
            "unitedkingdom" -> return R.mipmap.trice_vpn_unitedkingdom
            "unitedstates" -> return R.mipmap.trice_vpn_unitedstates
            "australia" -> return R.mipmap.trice_vpn_australia
            else -> return R.mipmap.super_fast_servers
        }
    }

}