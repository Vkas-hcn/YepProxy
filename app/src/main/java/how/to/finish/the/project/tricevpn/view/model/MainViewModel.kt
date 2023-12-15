package how.to.finish.the.project.tricevpn.view.model

import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.os.Bundle
import android.util.Log
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.view.animation.Animation
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import how.to.finish.the.project.tricevpn.base.BaseViewModel
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import com.github.shadowsocks.preference.DataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.blinkt.openvpn.api.IOpenVPNAPIService
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.base.App
import how.to.finish.the.project.tricevpn.base.BaseAdom
import how.to.finish.the.project.tricevpn.databinding.ActivityMainBinding
import how.to.finish.the.project.tricevpn.hlep.AdUtils
import how.to.finish.the.project.tricevpn.hlep.DataUtils
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG
import how.to.finish.the.project.tricevpn.hlep.DataUtils.getServiceFlag
import how.to.finish.the.project.tricevpn.hlep.ServiceBean
import how.to.finish.the.project.tricevpn.hlep.ServiceData
import how.to.finish.the.project.tricevpn.hlep.YepTimerUtils
import how.to.finish.the.project.tricevpn.view.ad.YepLoadConnectAd
import how.to.finish.the.project.tricevpn.view.ad.YepLoadHomeAd
import how.to.finish.the.project.tricevpn.view.ui.MainActivity
import how.to.finish.the.project.tricevpn.view.ui.ServiceActivity
import how.to.finish.the.project.tricevpn.view.utils.MainFun
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.io.BufferedReader
import java.io.InputStreamReader

class MainViewModel : BaseViewModel() {

    //重复点击
    var repeatClick = false
    var jobRepeatClick: Job? = null

    // 跳转结果页
    var liveJumpResultsPage = MutableLiveData<Bundle>()
    val connection = ShadowsocksConnection(true)

    // 是否返回刷新服务器
    var whetherRefreshServer = false
    var jobNativeAdsYep: Job? = null
    var jobStartYep: Job? = null

    //当前执行连接操作
    var performConnectionOperations: Boolean = false

    //点击之前的状态
    var clickPreviousStatus: Boolean = false

    //未连接进入
    var isConnectedToEnterYep = false
    var nowClickState: Int = 1

    var mService: IOpenVPNAPIService? = null
    lateinit var requestPermissionForResultVPN: ActivityResultLauncher<Intent?>

    companion object {
        var stateListener: ((BaseService.State) -> Unit)? = null
    }

    fun shareUrl(activity: MainActivity) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "https://play.google.com/store/apps/details?id=${activity.packageName}"
        )
        sendIntent.type = "text/plain"
        val shareIntent = Intent.createChooser(sendIntent, null)
        activity.startActivity(shareIntent)
    }

    fun updateUrl(activity: MainActivity) {
        activity.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=${activity.packageName}")
            )
        )
    }


    fun initToolbar(activity: AppCompatActivity, binding: ActivityMainBinding) {
//        binding.mainTitle.imgBack.setOnClickListener {
//            if (!binding.viewGuideMask.isVisible) {
//                if (!(clickPreviousStatus == "Stopped" && binding.vpnJump == true)) {
//                    //非连接过程中
//                    binding.sidebarShowsYep = true
//                }
//                if ((clickPreviousStatus == "Connected" && state.canStop && binding.vpnJump == true)) {
//                    //断开过程中(前两秒)
//                    jobStartYep?.cancel() // 取消执行方法的协程
//                    jobStartYep = null
//                    changeOfVpnStatus(activity, binding, 2)
//                }
//            }
//        }
    }

    fun initData(
        activity: MainActivity,
        binding: ActivityMainBinding,
        call: ShadowsocksConnection.Callback
    ) {
//        if (MainFun.isIllegalIp()) {
//            MainFun.isBulletBoxUsed(activity)
//            return
//        }

        // 设置状态
        changeState(BaseService.State.Idle, activity)

        // 连接服务
        connection.connect(activity, call)

        // 注册数据改变监听
        DataStore.publicStore.registerChangeListener(activity)

        // 初始化服务数据
        if (YepTimerUtils.isStopThread) {
            initializeServerData()
        } else {
            val serviceData = DataUtils.connect_vpn
            val currentServerData: ServiceBean =
                Gson().fromJson(serviceData, object : TypeToken<ServiceBean?>() {}.type)
            setFastInformation(currentServerData, binding)
        }
        BaseAdom.getHomeInstance().whetherToShowYep = false
        if (!AdUtils.blockAdUsers()) {
            binding.showAd = 2
        } else {
            binding.showAd = 0
        }
    }


    fun changeState(
        state: BaseService.State = BaseService.State.Idle,
        activity: AppCompatActivity,
        vpnState: Boolean = false,
    ) {
        connectionStatusJudgment(vpnState, activity)
        stateListener?.invoke(state)
    }

    fun jumpToServerList(activity: AppCompatActivity) {
        activity.lifecycleScope.launch {
            if (activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
                return@launch
            }
            val bundle = Bundle()
            if (App.vpnState) {
                bundle.putBoolean(DataUtils.whetherYepConnected, true)
            } else {
                bundle.putBoolean(DataUtils.whetherYepConnected, false)
            }
            BaseAdom.getBackInstance().advertisementLoadingYep(activity)
            val serviceData = DataUtils.connect_vpn
            bundle.putString(DataUtils.currentYepService, serviceData)
            val intent = Intent(activity, ServiceActivity::class.java)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, 0x33)
        }
    }


    fun setFastInformation(meteorVpnBean: ServiceBean, binding: ActivityMainBinding) {
        if (meteorVpnBean.best) {
            binding.aivFlag.setImageResource("Fast Server".getServiceFlag())
            binding.tvCountry.text = "Fast Server"
        } else {
            binding.aivFlag.setImageResource(meteorVpnBean.country.getServiceFlag())
            binding.tvCountry.text = meteorVpnBean.country
        }
    }

    fun startTheJudgment(activity: AppCompatActivity) {
        if (MainFun.isAppOnline(activity)) {
            startVpn(activity)
        } else {
            Toast.makeText(activity, "Please check your network connection", Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * 启动VPN
     */
    private fun startVpn(activity: AppCompatActivity) {
//        if (MainFun.isIllegalIp()) {
//            MainFun.isBulletBoxUsed(activity)
//            return
//        }
        if (DataUtils.recently_nums.isEmpty()) {
            ServiceData.saveRecentlyList("0")
        }

        jobStartYep = activity.lifecycleScope.launch {
            clickPreviousStatus = App.vpnState
            nowClickState = if (App.vpnState) {
                2
            } else {
                0
            }
            changeOfVpnStatus(activity as MainActivity, 1)
            delay(2000)
            connectVpn(activity)
            loadYepAdvertisements(activity)
        }
    }

    private fun connectVpn(activity: MainActivity) {
        if (!App.vpnState) {
            if (activity.binding.agreement == 2) {
                mService?.let {
                    step2(activity, it)
                }
                Core.stopService()
            } else {
                mService?.disconnect()
                Core.startService()
            }
        }
    }


    fun disconnectVpn() {
        if (App.vpnState) {
            Log.e(TAG, "开始断开")
            Core.stopService()
        }
    }

    /**
     * 加载Yep广告
     */
    private suspend fun loadYepAdvertisements(activity: AppCompatActivity) {
        try {
            withTimeout(10000L) {
                delay(300L)
                while (true) {
                    if (!isActive) {
                        break
                    }
                    when (YepLoadConnectAd.displayConnectAdvertisementYep(
                        activity as MainActivity,
                        closeWindowFun = {
                            connectOrDisconnectYep(activity,true)
                            BaseAdom.getConnectInstance().advertisementLoadingYep(activity)
                        })) {
                        2 -> {
                            cancel()
                            jobStartYep = null
                        }

                        0 -> {
                            cancel()
                            connectOrDisconnectYep(activity)
                        }
                    }
                    delay(500L)
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.d(TAG, "connect---插屏超时")
            connectOrDisconnectYep(activity as MainActivity)
        }
    }


    fun connectOrDisconnectYep(activity: MainActivity, isOpenJump: Boolean = false) {

        if (nowClickState == 2) {
            mService?.disconnect()
            disconnectVpn()
            if (!App.startState) {
                MainFun.jumpResultsPageData(activity, false)
            }
            changeOfVpnStatus(activity, 0)
        }
        if (nowClickState == 0) {
            if(!isOpenJump){
                if (activity.binding.agreement == 2) {
                    return
                }
            }
            if (!App.startState) {
                MainFun.jumpResultsPageData(activity, true)
            }
            changeOfVpnStatus(activity, 2)
        }

    }

    /**
     * 连接状态判断
     */
    fun connectionStatusJudgment(
        vpnState: Boolean,
        activity: AppCompatActivity
    ) {
        val binding = (activity as MainActivity).binding
        when (vpnState) {
            true -> {
                // 连接成功
                connectionServerSuccessful(binding)
            }

            false -> {
                disconnectServerSuccessful(binding)
            }
        }
    }

    /**
     * 连接服务器成功
     */
    fun connectionServerSuccessful(binding: ActivityMainBinding) {
        binding.vpnState = 2
    }

    /**
     * 断开服务器
     */
    fun disconnectServerSuccessful(binding: ActivityMainBinding) {
        Log.e(TAG, "断开服务器")
        binding.vpnState = 0

    }

    /**
     * vpn状态变化
     * 是否连接
     */
    fun changeOfVpnStatus(
        activity: MainActivity,
        stateInt: Int
    ) {
        val binding = activity.binding
        binding.vpnState = stateInt
        Log.e(TAG, "changeOfVpnStatus: ${stateInt}")
        when (stateInt) {
            0 -> {
                //断开
                MainFun.stopRotation(binding.imageView3)
                YepTimerUtils.endTiming()
                binding.imageView3.setImageResource(R.drawable.ic_connect)

            }

            1 -> {
                //连接中
                MainFun.rotateImageViewInfinite(binding.imageView3, 800)
                binding.imageView3.setImageResource(R.drawable.ic_main_loading)
            }

            2 -> {
                //连接成功
                MainFun.stopRotation(binding.imageView3)
                YepTimerUtils.startTiming()
                binding.imageView3.setImageResource(R.drawable.ic_disconnect)

            }

            else -> {
                //未知
                MainFun.stopRotation(binding.imageView3)
            }

        }
    }

    fun showHomeAd(activity: MainActivity) {
        activity.lifecycleScope.launch {
            delay(300)
            if (activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
                return@launch
            }
            val adHomeData = BaseAdom.getHomeInstance().appAdDataYep
            if (adHomeData == null) {
                BaseAdom.getHomeInstance().advertisementLoadingYep(activity)
            }
            while (isActive) {
                if (adHomeData != null) {
                    YepLoadHomeAd.setDisplayHomeNativeAdYep(activity)
                    cancel()
                    break
                }
                delay(500)
            }
        }
    }

    val liveInitializeServerData: MutableLiveData<ServiceBean> by lazy {
        MutableLiveData<ServiceBean>()
    }

    val liveNoUpdateServerData: MutableLiveData<ServiceBean> by lazy {
        MutableLiveData<ServiceBean>()
    }

    val liveUpdateServerData: MutableLiveData<ServiceBean?> by lazy {
        MutableLiveData<ServiceBean?>()
    }

    var currentServerData: ServiceBean = ServiceBean()

    var afterDisconnectionServerData: ServiceBean = ServiceBean()

//    //跳转结果页
//    val liveJumpResultsPageFun: MutableLiveData<Bundle> by lazy {
//        MutableLiveData<Bundle>()
//    }

    fun initializeServerData() {
        val bestData = ServiceData.getFastVpn()
        ProfileManager.getProfile(DataStore.profileId).let {
            if (it != null) {
                ProfileManager.updateProfile(MainFun.setSkServerData(it, bestData))
            } else {
                val profile = Profile()
                ProfileManager.createProfile(MainFun.setSkServerData(profile, bestData))
            }
        }
        bestData.best = true
        DataStore.profileId = 1L
        currentServerData = bestData
        val serviceData = Gson().toJson(currentServerData)
        DataUtils.connect_vpn = serviceData
        liveInitializeServerData.postValue(bestData)
    }

    fun updateSkServer(isConnect: Boolean) {
        val skServiceBean = Gson().fromJson<ServiceBean>(
            DataUtils.connect_vpn,
            object : TypeToken<ServiceBean?>() {}.type
        )
        ProfileManager.getProfile(DataStore.profileId).let {
            if (it != null) {
                MainFun.setSkServerData(it, skServiceBean)
                ProfileManager.updateProfile(it)
            } else {
                ProfileManager.createProfile(Profile())
            }
        }
        DataStore.profileId = 1L
        if (isConnect) {
            afterDisconnectionServerData = skServiceBean
            liveUpdateServerData.postValue(skServiceBean)
        } else {
            currentServerData = skServiceBean
            val serviceData = Gson().toJson(currentServerData)
            DataUtils.connect_vpn = serviceData
            liveNoUpdateServerData.postValue(skServiceBean)
        }
    }

    fun step2(activity: MainActivity, server: IOpenVPNAPIService): Job? {
        if (checkVPNPermission(activity)) {
            val job = MainScope().launch(Dispatchers.IO) {
                val data = ServiceData.getAllVpnListData()[0]
                runCatching {
                    val conf = activity.assets.open("fast_yepproxy.ovpn")
                    val br = BufferedReader(InputStreamReader(conf))
                    val config = StringBuilder()
                    var line: String?
                    while (true) {
                        line = br.readLine()
                        if (line == null) break
                        if (line.contains("remote 43", true)) {
                            line = "remote ${data.ip} 443"
                        } else if (line.contains("wrongpassword", true)) {
                            line = data.password
                        } else if (line.contains("cipher AES-256-GCM", true)) {
                        }
                        config.append(line).append("\n")
                    }
                    br.close()
                    conf.close()
                    Log.e(TAG, "step2: =${config}")
                    server.startVPN(config.toString())

                }.onFailure {
                }
            }
            return job
        } else {
            VpnService.prepare(activity).let {
                requestPermissionForResultVPN.launch(it)
            }
        }
        return null
    }

    private fun checkVPNPermission(activity: MainActivity): Boolean {
        VpnService.prepare(activity).let {
            return it == null
        }
    }
    fun isConnectGuo(activity: MainActivity): Boolean{
        return !(nowClickState ==0 && activity.binding.vpnState == 1)
    }

    fun clickChange(activity: MainActivity,nextFun:()->Unit){
        if (isConnectGuo(activity)) {
            nextFun()
        } else {
            Toast.makeText(
                activity,
                "VPN is connecting. Please try again later.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}