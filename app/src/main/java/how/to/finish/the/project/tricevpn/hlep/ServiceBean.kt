package how.to.finish.the.project.tricevpn.hlep

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
data class ServiceBean(
    @SerializedName("gg_a_a")
    val password: String="",

    @SerializedName("gg_a_b")
    val port: String="",

    @SerializedName("gg_a_y")
    val agreement: String="",

    @SerializedName("gg_a_p")
    var country: String="",

    @SerializedName("gg_a_c")
    var city: String="",

    @SerializedName("gg_a_o")
    val ip: String="",

    var best: Boolean =false,
    var smart: Boolean=false,
    var check: Boolean=false,
):Serializable
