package how.to.finish.the.project.tricevpn.view.ui

import android.view.KeyEvent
import androidx.lifecycle.lifecycleScope
import how.to.finish.the.project.tricevpn.base.BaseActivity
import how.to.finish.the.project.tricevpn.view.model.MainViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.VpnService
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleObserver
import androidx.preference.PreferenceDataStore
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.DataStore
import com.github.shadowsocks.preference.OnPreferenceDataStoreChangeListener
import com.github.shadowsocks.utils.Key
import com.github.shadowsocks.utils.StartService
import com.google.gson.Gson
import de.blinkt.openvpn.api.ExternalOpenVPNService
import de.blinkt.openvpn.api.IOpenVPNAPIService
import de.blinkt.openvpn.api.IOpenVPNStatusCallback
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.base.App
import how.to.finish.the.project.tricevpn.databinding.ActivityMainBinding
import how.to.finish.the.project.tricevpn.hlep.AdUtils
import how.to.finish.the.project.tricevpn.hlep.DataUtils
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG
import how.to.finish.the.project.tricevpn.hlep.ServiceData
import how.to.finish.the.project.tricevpn.hlep.YepTimerUtils
import how.to.finish.the.project.tricevpn.net.YepOkHttpUtils
import how.to.finish.the.project.tricevpn.view.utils.MainFun
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(),
    ShadowsocksConnection.Callback,
    OnPreferenceDataStoreChangeListener, LifecycleObserver {

    override fun getLayoutRes(): Int = R.layout.activity_main

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java
    private val connection = ShadowsocksConnection(true)
    private var timeJob: Job? = null
    override fun init() {
        mainClick()
        initVpnSetting()
        showTimeTv()
        setServiceData()
    }

    private fun setServiceData() {
        viewModel.liveInitializeServerData.observe(this) {
            it?.let {
                viewModel.setFastInformation(it, binding)
            }
        }

        viewModel.liveUpdateServerData.observe(this) {
            it?.let {
                viewModel.whetherRefreshServer = true
                toConnectVpn()
            }
        }
        viewModel.liveNoUpdateServerData.observe(this) {
            it?.let {
                viewModel.whetherRefreshServer = false
                viewModel.setFastInformation(it, binding)
                toConnectVpn()
            }

        }
    }

    private fun mainClick() {
        binding.showGuide = true
        binding.agreement = DataUtils.agreement_type
        binding.viewBg.setOnClickListener {
        }
        binding.atvPp.setOnClickListener {
            launchActivity(PpWebActivity::class.java)
        }
        binding.atvUpdate.setOnClickListener {
            viewModel.updateUrl(this)
        }
        binding.atvShare.setOnClickListener {
            viewModel.shareUrl(this)
        }
        binding.imgMenu.setOnClickListener {
            viewModel.clickDisConnect(this, nextFun = {
                viewModel.clickChange(this, nextFun = {
                    binding.dlMain.open()
                })
            })
        }
        binding.llAuto.setOnClickListener {
            viewModel.clickChange(this, nextFun = {
                checkAgreement(0)
            })
        }
        binding.llSs.setOnClickListener {
            viewModel.clickChange(this, nextFun = {
                checkAgreement(1)
            })
        }
        binding.llOpen.setOnClickListener {

            viewModel.clickChange(this, nextFun = {
                checkAgreement(2)
            })
        }
        binding.llService.setOnClickListener {
            viewModel.clickChange(this, nextFun = {
                viewModel.jumpToServerList(this)
            })
        }
    }

    private fun checkAgreement(type: Int) {
        if (App.vpnState) {
            if (type == 2 && binding.agreement != 2) {
                showSwitching(type)
                return
            }
            if (type != 2 && binding.agreement == 2) {
                showSwitching(type)
                return
            }
            binding.agreement = type

        } else {
            binding.agreement = type
        }
        Log.e(TAG, "DataUtils.agreement_type1=${DataUtils.agreement_type}")
    }

    private fun showSwitching(type: Int) {
        val dialogVpn: androidx.appcompat.app.AlertDialog =
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Tips")
                .setMessage("switching the connection mode will disconnect the current connection whether to continue")
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    toConnectVpn()
                    binding.agreement = type
                    Log.e(TAG, "DataUtils.agreement_type2=${DataUtils.agreement_type}")
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }.create()
        dialogVpn.setCancelable(false)
        dialogVpn.show()
    }

    fun toClickConnect(v: View) {
        if (binding.vpnState == 1) {
            return
        }
        lifecycleScope.launch {
            Log.e(TAG, "toClickConnect: 111")
            toConnectVpn()
        }
    }

    private fun toConnectVpn() {
        binding.showGuide = false
        if (MainFun.isAppOnline(this)) {
            if (!App.vpnState) {
                DataUtils.agreement_type = binding?.agreement!!
            }
            if (binding.agreement == 2) {
                viewModel.startTheJudgment(this)
            } else {
                connect.launch(null)
            }
        } else {
            Toast.makeText(this, "Please check your network connection", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun initVpnSetting() {
        val data = AdUtils.spoilerOrNot()
        DataUtils.rl_data = data
        viewModel.initData(this, binding, this)
        bindService(
            Intent(this, ExternalOpenVPNService::class.java),
            mConnection,
            BIND_AUTO_CREATE
        )
        viewModel.requestPermissionForResultVPN =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                requestPermissionForResult(it)
            }
    }


    private val connect = registerForActivityResult(StartService()) {
        lifecycleScope.launch(Dispatchers.IO) {
            YepOkHttpUtils().getCurrentIp()
        }
        if (it) {
            Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.startTheJudgment(this)
        }
    }


    private fun requestPermissionForResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            viewModel.startTheJudgment(this)
            Log.e(TAG, "requestPermissionForResult: 1")
        } else {
            Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "requestPermissionForResult: 2")
        }
    }

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName?,
            service: IBinder?,
        ) {
            viewModel.mService = IOpenVPNAPIService.Stub.asInterface(service)
            try {
                viewModel.mService?.registerStatusCallback(mCallback)
                Log.e("open vpn mService", "mService onServiceConnected")
            } catch (e: Exception) {
                Log.e("open vpn error", e.message.toString())
            }
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            Log.e("open vpn mService", "mService onServiceDisconnected")
            viewModel.mService = null
        }
    }
    private val mCallback = object : IOpenVPNStatusCallback.Stub() {
        override fun newStatus(uuid: String?, state: String?, message: String?, level: String?) {
            // NOPROCESS 未连接 // CONNECTED 已连接
            // RECONNECTING 尝试重新链接 // EXITING 连接中主动掉用断开
            Log.e(
                TAG,
                "newStatus: state=$state;message=$message;agreement=${DataUtils.agreement_type}"
            )
            if (DataUtils.agreement_type != 2) {
                return
            }
            when (state) {
                "CONNECTED" -> {
                    binding.showGuide = false
                    App.vpnState = true
                    viewModel.connectOrDisconnectYep(this@MainActivity, true)
                    viewModel.changeState(
                        state = BaseService.State.Idle,
                        this@MainActivity,
                        App.vpnState
                    )
                }

                "RECONNECTING", "EXITING", "CONNECTRETRY" -> {
                    viewModel.mService?.disconnect()
                }

                "NOPROCESS" -> {
                    viewModel.mService?.disconnect()
                    App.vpnState = false
                    viewModel.connectOrDisconnectYep(this@MainActivity, true)
                    viewModel.changeState(
                        state = BaseService.State.Idle,
                        this@MainActivity,
                        App.vpnState
                    )
                }


                else -> {}
            }

        }

    }


    fun showTimeTv() {
        timeJob?.cancel()
        timeJob = null
        timeJob = lifecycleScope.launch {
            while (isActive) {
                binding.atvTime.text = YepTimerUtils.getTiming()
                delay(1000L)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        connection.bandwidthTimeout = 500
    }

    override fun onResume() {
        super.onResume()
        handleWarmBoot()
    }

    private fun handleWarmBoot() {
        viewModel.showHomeAd(this)
    }

    private fun handleYepTimerLock() {
//        if (!App.vpnState && viewModel.nowClickState != 1) {
//            viewModel.changeOfVpnStatus(this, 0)
//        }
        if (App.vpnState) {
            binding.showGuide = false
            binding.vpnState = 2
            if (binding.atvTime.text.toString() == "00:00:00") {
                YepTimerUtils.startTiming()
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        connection.bandwidthTimeout = 0
        viewModel.stopOperate(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        DataStore.publicStore.unregisterChangeListener(this)
        connection.disconnect(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x22 && viewModel.whetherRefreshServer) {
            viewModel.setFastInformation(viewModel.afterDisconnectionServerData, binding)
            val serviceData = Gson().toJson(viewModel.afterDisconnectionServerData)
            DataUtils.connect_vpn = serviceData
            viewModel.currentServerData = viewModel.afterDisconnectionServerData
        }
        if (requestCode == 0x33) {
            Log.e(TAG, "onActivityResult: =${App.serviceState}")
            when (App.serviceState) {
                0 -> {
                    viewModel.updateSkServer(false)
                }

                1 -> {
                    viewModel.updateSkServer(true)
                }
            }
            App.serviceState = -1
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.showGuide == true) {
                binding.showGuide = false
            } else {
                viewModel.clickChange(this, nextFun = {
                    finish()
                })
            }
        }
        return true
    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        App.vpnState = state.canStop
        Log.e(TAG, "stateChanged: App.vpnState=${App.vpnState}")
        viewModel.changeState(state, this)
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        val state = BaseService.State.values()[service.state]
        setSsVpnState(state.canStop)
    }

    private fun setSsVpnState(canStop: Boolean) {
        if (DataUtils.agreement_type != 2) {
            App.vpnState = canStop
            Log.e(TAG, "setSsVpnState: App.vpnState=${App.vpnState}")
            handleYepTimerLock()
        }
    }

    fun setOpenVpnState(canStop: Boolean) {
        if (DataUtils.agreement_type != 2) {
            App.vpnState = canStop
        }
    }

    override fun onPreferenceDataStoreChanged(store: PreferenceDataStore, key: String) {
        when (key) {
            Key.serviceMode -> {
                connection.disconnect(this)
                connection.connect(this, this)
            }
        }
    }
}