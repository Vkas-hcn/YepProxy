package how.to.finish.the.project.tricevpn.hlep
import androidx.annotation.Keep

@Keep
data class IpBean(
    var country: String?=null,
    var country_code: String?=null,
    var country_code3: String?=null,
    var ip: String?=null
)
