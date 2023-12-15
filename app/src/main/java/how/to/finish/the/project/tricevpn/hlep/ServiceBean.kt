package how.to.finish.the.project.tricevpn.hlep

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
data class ServiceBean(
    @SerializedName("weryep")
    val password: String="",

    @SerializedName("arryep")
    val port: String="",

    @SerializedName("ureyep")
    val agreement: String="",

    @SerializedName("veryyep")
    var country: String="",

    @SerializedName("scientyep")
    var city: String="",

    @SerializedName("stfulyep")
    val ip: String="",

    var best: Boolean =false,
    var smart: Boolean=false,
    var check: Boolean=false,
):Serializable
