package how.to.finish.the.project.tricevpn.baseeeeee

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import how.to.finish.the.project.tricevpn.cloakkkkk.BlackUtils
import how.to.finish.the.project.tricevpn.dataaaaaa.TriceDataHelper
import how.to.finish.the.project.tricevpn.mainnnnnnn.SplashActivity
import how.to.finish.the.project.tricevpn.uitlllll.IPUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class BaseApp : Application(), Application.ActivityLifecycleCallbacks {
    companion object {
        var isInterrupt: Boolean = false
        var isActivityBack: Boolean = false
        var application: BaseApp? = null
        fun getInstance(): BaseApp {
            if (application == null) application = BaseApp()
            return application ?: BaseApp()
        }
        var actttttttt = 0
        var acllllllll = mutableListOf<Activity>()
        var eatttttt = 0L
        var bbbbbbbbbbbbbbg: Boolean = false

    }


    override fun onCreate() {
        application = this
        super.onCreate()
        IPUtils().checkIp()
        registerActivityLifecycleCallbacks(this)
        if (AppConstant.uuid_trice.isEmpty()) {
            AppConstant.uuid_trice = java.util.UUID.randomUUID().toString()
        }
        GlobalScope.launch(Dispatchers.IO) {
            getRecordNetData(this@BaseApp)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (packageName != getProcessName()) {
                WebView.setDataDirectorySuffix(getProcessName())
            }
        }
        TriceDataHelper.initVpnFb()
        BlackUtils().checkIsLimitCloak()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        acllllllll.add(activity)

    }

    override fun onActivityStarted(activity: Activity) {
        actttttttt++
        if (bbbbbbbbbbbbbbg) {
            bbbbbbbbbbbbbbg = false
            if ((System.currentTimeMillis() - eatttttt) / 1000 > 3) {
                val intent = Intent(activity, SplashActivity::class.java)
                activity.startActivity(intent)
                if (activity is SplashActivity) activity.finish()
            } else if (isActivityBack) {
                isActivityBack = false
                val intent = Intent(activity, SplashActivity::class.java)
                activity.startActivity(intent)
                if (activity is SplashActivity) activity.finish()
            } else if (activity is SplashActivity) {
                val intent = Intent(activity, SplashActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        actttttttt--
        if (actttttttt == 0) {
            bbbbbbbbbbbbbbg = true
            eatttttt = System.currentTimeMillis()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        acllllllll.remove(activity)
    }
    private fun getRecordNetData(context: Context) {
        if (AppConstant.black_trice.isNotEmpty()) {
            return
        }
        val map = AppConstant.cloakData(context)
        Log.e("trice", "map-data=${map} ")
        val client = OkHttpClient()
        val urlBuilder = AppConstant.black_url.toHttpUrl().newBuilder()
        map.forEach { entry ->
            urlBuilder.addEncodedQueryParameter(
                entry.key,
                URLEncoder.encode(entry.value, StandardCharsets.UTF_8.toString())
            )
        }
        val request = Request.Builder()
            .get()
            .tag(map)
            .url(urlBuilder.build())
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: Call, response: Response) {
                GlobalScope.launch(Dispatchers.IO) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful && responseBody != null) {
                        AppConstant.black_trice = responseBody
                        Log.e("trice", "blacklist results=${AppConstant.black_trice} ")
                    } else {
                        delay(10001)
                        getRecordNetData(context)
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                GlobalScope.launch(Dispatchers.IO) {
                    delay(10001)
                    getRecordNetData(context)
                }
            }
        })
    }
}
