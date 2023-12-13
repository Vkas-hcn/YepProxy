package how.to.finish.the.project.tricevpn.base

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import how.to.finish.the.project.tricevpn.view.ui.StartActivity
import com.tencent.mmkv.MMKV
import android.os.Process
import android.util.Log
import android.webkit.WebView
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG

class App: Application(), Application.ActivityLifecycleCallbacks {
    companion object {
        var num = 0
        var nums = mutableListOf<Activity>()
        var timeData = 0L
        var startState: Boolean = false
        val mmkvDua by lazy {
            MMKV.mmkvWithID("Dual", MMKV.MULTI_PROCESS_MODE)
        }
    }


    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Log.e("weqeqweqwe", "onCreate: ${e.message}", )
        }
        val myPid = Process.myPid()
        val activityManager =
            this.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
        val processInfoList = activityManager.runningAppProcesses
        val packageName = this.packageName
        for (info in processInfoList) {
            if (info!!.pid == myPid && packageName == info.processName) {
                Log.e(TAG, "onCreate:=${packageName}", )

                registerActivityLifecycleCallbacks(this)
//                MobileAds.initialize(this) {}
//                Firebase.initialize(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (this.packageName != Application.getProcessName()) {
                        WebView.setDataDirectorySuffix(Application.getProcessName())
                    }
                }

            }
        }

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        nums.add(activity)

    }

    override fun onActivityStarted(activity: Activity) {
        num++
        if (startState) {
            startState = false
            if ((System.currentTimeMillis() - timeData) / 1000 > 3) {
                val intent = Intent(activity, StartActivity::class.java)
                activity.startActivity(intent)
                if (activity is StartActivity) activity.finish()
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        num--
        if (num == 0) {
            startState = true
            timeData = System.currentTimeMillis()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        nums.remove(activity)
    }
}
