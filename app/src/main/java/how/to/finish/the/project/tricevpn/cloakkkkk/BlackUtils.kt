package how.to.finish.the.project.tricevpn.cloakkkkk

import android.os.Build
import android.provider.Settings
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import how.to.finish.the.project.tricevpn.BuildConfig
import how.to.finish.the.project.tricevpn.baseeeeee.BaseApp
import how.to.finish.the.project.tricevpn.uitlllll.NetInterceptor
import how.to.finish.the.project.tricevpn.uitlllll.SPUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

open class BlackUtils {
    private val netInterceptor = NetInterceptor()
    private fun createOkhttp(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val httpLogging = HttpLoggingInterceptor()
            httpLogging.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(httpLogging)
        }

        return builder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
            .addNetworkInterceptor(netInterceptor).readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true).build()

    }

    private val retrofitCloak by lazy {
        Retrofit.Builder().client(createOkhttp())
            .baseUrl("https://waterway.galaxyguard.net/takeoff/")
            .addConverterFactory(StringConverterFactory())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
    private val cloakApiService: CloakApiService by lazy {
        retrofitCloak.create(CloakApiService::class.java)
    }


    companion object {
        const val USER_IS_BLACK = "usersssss"
        const val CLOAK_STRING_DDD = "awefawef"
        const val GET_CLOAK = "afwefawe"
        var gaid = ""
    }


    open fun checkIsLimitCloak(): Boolean {
        val isHaveCloak = SPUtil(BaseApp.getInstance().applicationContext).getBoolean(
            GET_CLOAK, false
        )
        val isBlack = SPUtil(BaseApp.getInstance().applicationContext).getBoolean(
            USER_IS_BLACK, true
        )
        return if (isHaveCloak && isBlack) true
        else if (!isBlack) false
        else {
            circleToCheckCloak()
            true
        }
    }

    private fun circleToCheckCloak() {
        var i = 0
        MainScope().launch {
            while (true) {
                if (!SPUtil(BaseApp.getInstance().applicationContext).getBoolean(
                        GET_CLOAK, false
                    )
                ) {
                    getCloakType(0)
                } else {
                    break
                }
                if (i == 3) break
                delay(10000)
                i++
            }
            while (true) {
                delay(300)
                if (SPUtil(BaseApp.getInstance().applicationContext).getBoolean(
                        GET_CLOAK, false
                    )
                ) break
                if (BaseApp.bbbbbbbbbbbbbbg) i = 0
            }
        }

    }

    private fun getAndroidID(): String {
        return Settings.System.getString(
            BaseApp.getInstance().contentResolver, Settings.Secure.ANDROID_ID
        )
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun getCloakType(a: Int) {
        MainScope().launch {
            val androidId = getAndroidID()
            val clientTime = System.currentTimeMillis()
            val mobileModel = Build.MODEL
            val appPackageName = "com.galaxy.guard.fast.link.privacy.pro"
            val mobileSDKVersion = BuildConfig.VERSION_CODE

            GlobalScope.launch {
                var advertisingId = ""
                if (gaid.isEmpty()) {
                    try {
                        val adInfo: AdvertisingIdClient.Info =
                            AdvertisingIdClient.getAdvertisingIdInfo(BaseApp.getInstance())
                        advertisingId = adInfo.id ?: ""
                        gaid = adInfo.id ?: ""
                    } catch (e: Exception) {
                    }
                } else advertisingId = gaid

                withContext(Dispatchers.IO) {
                    val call: Call<String> = cloakApiService.getString(
                        androidId,
                        nag = clientTime.toString(),
                        diego = mobileModel,
                        brenda = appPackageName,
                        inexpert = mobileSDKVersion.toString(),
                        gaid = advertisingId,
                        pap = androidId,
                        antigone = "nucleus",
                        reich = BuildConfig.VERSION_NAME,
                    )
                    call.enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            if (response.isSuccessful) {
                                when (response.body().toString()) {
                                    "indy" -> {
                                        SPUtil(BaseApp.getInstance().applicationContext).putBoolean(
                                            GET_CLOAK, true
                                        )
                                        SPUtil(BaseApp.getInstance().applicationContext).putBoolean(
                                            USER_IS_BLACK, false
                                        )
                                        SPUtil(BaseApp.getInstance().applicationContext).putInt(
                                            CLOAK_STRING_DDD, a
                                        )
                                    }

                                    "caustic" -> {
                                        SPUtil(BaseApp.getInstance().applicationContext).putBoolean(
                                            GET_CLOAK, true
                                        )
                                        SPUtil(BaseApp.getInstance().applicationContext).putBoolean(
                                            USER_IS_BLACK, true
                                        )
                                        SPUtil(BaseApp.getInstance().applicationContext).putInt(
                                            CLOAK_STRING_DDD, a
                                        )
                                    }

                                    else -> {
                                        SPUtil(BaseApp.getInstance().applicationContext).putBoolean(
                                            USER_IS_BLACK, true
                                        )
                                        SPUtil(BaseApp.getInstance().applicationContext).putInt(
                                            CLOAK_STRING_DDD, a
                                        )
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {

                        }
                    })
                }
            }
        }


    }

    interface CloakApiService {
        @GET("symbiote")
        @Headers("Content-Type: application/json")
        fun getString(
            @Query("magma") magma: String,//distinct_id
            @Query("nag") nag: String,//client_ts
            @Query("diego") diego: String,//device modep
            @Query("brenda") brenda: String,//bundle_id
            @Query("inexpert") inexpert: String,//os_version
            @Query("gaid") gaid: String,
            @Query("pap") pap: String,
            @Query("antigone") antigone: String,
            @Query("reich") reich: String,
        ): Call<String>
    }


}
