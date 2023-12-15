package how.to.finish.the.project.tricevpn.view.ui

import android.util.Log
import android.view.KeyEvent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.github.shadowsocks.Core.activity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import how.to.finish.the.project.tricevpn.base.BaseActivity
import how.to.finish.the.project.tricevpn.base.BaseViewModel
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.base.BaseAdom
import how.to.finish.the.project.tricevpn.databinding.ActivityEndBinding
import how.to.finish.the.project.tricevpn.hlep.DataUtils
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG
import how.to.finish.the.project.tricevpn.hlep.ServiceBean
import how.to.finish.the.project.tricevpn.view.ad.YepLoadBackAd
import how.to.finish.the.project.tricevpn.view.ad.YepLoadEndAd
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class EndActivity : BaseActivity<BaseViewModel, ActivityEndBinding>() {

    override fun getLayoutRes(): Int = R.layout.activity_end

    override fun getViewModelClass(): Class<BaseViewModel> = BaseViewModel::class.java
    private var isConnectionYep: Boolean = false

    //当前服务器
    private lateinit var serverBeanYep: ServiceBean

    override fun init() {
        BaseAdom.getResultInstance().whetherToShowYep = false
        val bundle = intent.extras
        isConnectionYep = bundle?.getBoolean(DataUtils.connectionYepStatus) == true
        serverBeanYep = Gson().fromJson(
            bundle?.getString(DataUtils.serverYepInformation),
            object : TypeToken<ServiceBean?>() {}.type
        )
        binding.imageView.setOnClickListener {
            returnToHomePage()
        }
        binding.vpnState = isConnectionYep
    }

    override fun onResume() {
        super.onResume()
        showEndAd()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            returnToHomePage()
        }
        return true
    }

    private fun returnToHomePage() {
        if (YepLoadBackAd.displayBackAdvertisementYep(this, cloneAd = {
                finish()
            }) != 2) {
            finish()
        }
    }

    private fun showEndAd() {
        lifecycleScope.launch {
            delay(200)
            if (lifecycle.currentState != Lifecycle.State.RESUMED) {
                return@launch
            }
            val adEndData = BaseAdom.getResultInstance().appAdDataYep
            if (adEndData == null) {
                BaseAdom.getResultInstance().advertisementLoadingYep(this@EndActivity)
            }
            while (isActive) {
                if (adEndData != null) {
                    YepLoadEndAd.setDisplayEndNativeAdYep(this@EndActivity)
                    cancel()
                    break
                }
                delay(500)
            }
        }
    }
}