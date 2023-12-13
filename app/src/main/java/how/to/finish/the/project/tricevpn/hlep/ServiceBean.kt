package how.to.finish.the.project.tricevpn.hlep

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
data class ServiceBean(
    @SerializedName("onLu")
    val password: String,

    @SerializedName("onLo")
    val port: String,

    @SerializedName("onLi")
    val agreement: String,

    @SerializedName("onLp")
    var country: String,

    @SerializedName("onLl")
    var city: String,

    @SerializedName("onLm")
    val ip: String,

    var best: Boolean,
    var smart: Boolean,
    var check: Boolean,
):Serializable
