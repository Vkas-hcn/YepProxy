package how.to.finish.the.project.tricevpn.uitlllll

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.Gravity
import android.webkit.WebSettings
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatTextView
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.baseeeeee.AppConstant
import how.to.finish.the.project.tricevpn.baseeeeee.BaseAc
import how.to.finish.the.project.tricevpn.baseeeeee.BaseApp
import how.to.finish.the.project.tricevpn.mainnnnnnn.MainActivity
import how.to.finish.the.project.tricevpn.mainnnnnnn.SplashActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.IOException
import java.lang.reflect.Type
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

interface NetworkChangeListener {
    fun onNetworkChanged(isConnected: Boolean)
}

class NetworkChangeReceiver(private val listener: NetworkChangeListener?) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val connMgr: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        val isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected
        listener?.onNetworkChanged(isConnected)
    }
}

class IPUtils {

    private var retrofit: Retrofit? = null
    private val netInterceptor = NetInterceptor()

    private val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit =
                    Retrofit.Builder().client(createOkhttp()).baseUrl("https://ipapi.co/")
                        .addConverterFactory(GsonConverterFactory.create()).build()
            }
            return retrofit
        }

    private fun createOkhttp(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val httpLogging = HttpLoggingInterceptor()
        httpLogging.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(httpLogging)


        return builder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
            .addNetworkInterceptor(netInterceptor).readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true).build()

    }

    fun showIpDialog(activity: MainActivity) {
        AppConstant.lock_code.let {
            if (it.isEmpty()) {
                IPUtils().setIsBanded(activity)
                return
            } else {
                if (IPUtils().checkIpIsBanded(it.lowercase())) {
                    showDialog(activity)
                }
            }
        }
    }

    fun checkIp() {
        val apiService: ApiService = retrofitInstance!!.create(ApiService::class.java)
        val call: Call<KKKKKK> = apiService.getIPAddress1()
        call.enqueue(object : Callback<KKKKKK> {
            override fun onResponse(call: Call<KKKKKK>, response: Response<KKKKKK>) {
                val data: KKKKKK? = response.body()
                AppConstant.lock_code = data?.country_code?.lowercase() ?: ""
                Log.e("trice", "onResponse=${AppConstant.lock_code }")

            }

            override fun onFailure(call: Call<KKKKKK>, t: Throwable) {
                Log.e("trice","ip request failed${t.message.toString()}")
            }
        })
    }

    private fun setIsBanded(activity: BaseAc<*>) {
        activity.isShowBandedDialog = if (AppConstant.lock_code.isBlank()) {
            checkIpIsBandedForLanguage()
        } else {
            checkIpIsBanded(AppConstant.lock_code.lowercase())
        }
        if (activity.isShowBandedDialog && activity !is SplashActivity) {
            showDialog(activity)
        }

    }

    fun showDialog(activity: BaseAc<*>) {
        val customDialog = Dialog(activity, R.style.AppDialogStyle)
        val localLayoutParams = customDialog.window?.attributes
        localLayoutParams?.gravity = Gravity.CENTER
        customDialog.window?.attributes = localLayoutParams
        customDialog.setContentView(R.layout.dialog_check_ip)
        val confirmButton = customDialog.findViewById<AppCompatTextView>(R.id.ipConfirm)
        confirmButton.setOnClickListener {
            activity.finish()
            exitProcess(0)
        }
//        customDialog.show()
    }


    fun checkIpIsBanded(string: String?): Boolean {
        return if (string.isNullOrEmpty()) {
            checkIpIsBandedForLanguage()
        } else {
            when (string) {
                "cn",
                "hk", "ir", "mo" -> true

                else -> false
            }
        }
    }

    private fun checkIpIsBandedForLanguage(): Boolean {
        return when (Locale.getDefault().language.toLowerCase()) {
            "zh", "fa" -> true
            else -> false
        }
    }


}


class NetInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request: Request = chain.request().newBuilder()
            .addHeader("Connection", "close")
            .addHeader(
                "User-Agent",
                WebSettings.getDefaultUserAgent(BaseApp.getInstance().applicationContext)
            )
            .build()
        return chain.proceed(request)
    }
}

interface ApiService {
    @GET("json")
    fun getIPAddress1(): Call<KKKKKK>
}

@Keep
data class KKKKKK(
    var ip: String, var city: String, var country_code: String
)