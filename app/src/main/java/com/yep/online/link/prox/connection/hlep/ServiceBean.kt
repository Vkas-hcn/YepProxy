package com.yep.online.link.prox.connection.hlep

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
data class ServiceBean(
    @SerializedName("weryep")
    var password: String="",

    @SerializedName("arryep")
    var port: String="",

    @SerializedName("ureyep")
    var agreement: String="",

    @SerializedName("veryyep")
    var country: String="",

    @SerializedName("scientyep")
    var city: String="",

    @SerializedName("stfulyep")
    var ip: String="",

    var best: Boolean =false,
    var smart: Boolean=false,
    var check: Boolean=false,
):Serializable

@Keep
data class OnlineVpnBean(
    val code: Int,
    val `data`: Data,
    val msg: String
)

data class Data(
    val fdbTNoDnP: List<FdbTNoDnP>,
    val tvxF: List<TvxF>
)

data class FdbTNoDnP(
    val CkqNJTfzhs: String,
    val FpEEFA: String,
    val KqclNrS: List<String>,
    val KzHDx: String,
    val PaJsPE: String,
    val WgxG: String,
    val gDK: Int,
    val hnek: String,
    val oqjYzgx: String,
    val zbQy: String
)

data class TvxF(
    val CkqNJTfzhs: String,
    val FpEEFA: String,
    val KqclNrS: List<String>,
    val KzHDx: String,
    val PaJsPE: String,
    val WgxG: String,
    val gDK: Int,
    val hnek: String,
    val oqjYzgx: String,
    val zbQy: String
)
