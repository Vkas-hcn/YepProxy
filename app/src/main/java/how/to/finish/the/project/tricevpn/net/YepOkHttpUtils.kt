package how.to.finish.the.project.tricevpn.net

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import how.to.finish.the.project.tricevpn.hlep.DataUtils
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class YepOkHttpUtils {
    val client = OkHttpClientUtils()
    fun getCurrentIp() {
        try {
            client.get(DataUtils.ip_url, object : OkHttpClientUtils.Callback {
                override fun onSuccess(response: String) {
                    Log.e( TAG, "IP----->${response}")
                    DataUtils.ip_data = response
                }

                override fun onFailure(error: String) {
                }
            })
        } catch (e: Exception) {

        }

    }
    //发起黑名单请求
    fun getBlackList(activity: AppCompatActivity) {
        val map = CloakUtils.cloakJson(activity)
        Log.e("TAG", "Blacklist request data= $map",)
        try {
            client.getMap(DataUtils.clock_url, map,object : OkHttpClientUtils.Callback {
                override fun onSuccess(response: String) {
                    Log.e(TAG, "clock_data----->${response}")
                    DataUtils.clock_data = response
                }

                override fun onFailure(error: String) {
                    retry(activity)
                }
            })
        } catch (e: Exception) {
            retry(activity)
        }
    }
    fun retry(activity: AppCompatActivity) {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            delay(10000)
            getBlackList(activity)
        }
    }
}