package how.to.finish.the.project.tricevpn.hlep
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class YepAdBean(
    @SerializedName("ousyet")
    val open: String,

    @SerializedName("bedyet")
    val home: String,

    @SerializedName("queyet")
    val end: String,

    @SerializedName("tranyet")
    val connect: String,

    @SerializedName("furtyet")
    val back: String,
)
