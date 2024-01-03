package com.yep.online.link.prox.connection.hlep

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

    var loadCity: String,
    var showTheCity: String,

    var loadIp: String,
    var showIp: String,
)

@Keep
data class AdType(
    val id: String,
    val where: String,
    val name:String,
    val type: String,
)
