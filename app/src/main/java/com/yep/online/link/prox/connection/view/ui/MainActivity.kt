package com.yep.online.link.prox.connection.view.ui

import androidx.lifecycle.lifecycleScope
import com.yep.online.link.prox.connection.base.BaseActivity
import com.yep.online.link.prox.connection.view.model.MainViewModel
import kotlinx.coroutines.launch


import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
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
import com.yep.online.link.prox.connection.R
import com.yep.online.link.prox.connection.base.App
import com.yep.online.link.prox.connection.databinding.ActivityMainBinding
import com.yep.online.link.prox.connection.hlep.AdUtils
import com.yep.online.link.prox.connection.hlep.DataUtils
import com.yep.online.link.prox.connection.hlep.DataUtils.TAG
import com.yep.online.link.prox.connection.hlep.ServiceData
import com.yep.online.link.prox.connection.hlep.YepTimerUtils
import com.yep.online.link.prox.connection.net.CloakUtils
import com.yep.online.link.prox.connection.net.YepOkHttpUtils
import com.yep.online.link.prox.connection.view.utils.MainFun
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(),
    ShadowsocksConnection.Callback,
    OnPreferenceDataStoreChangeListener, LifecycleObserver {

    override fun getLayoutRes(): Int = R.layout.activity_main

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java
    private val connection = ShadowsocksConnection(true)
    private var timeJob: Job? = null
    override fun init() {
        if (MainFun.isLegalIpAddress(this)) {
            return
        }
        mainClick()
        initVpnSetting()
        showTimeTv()
        setServiceData()
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackFun()
            }
        })
    }

    fun onBackFun() {
        if (binding.showGuide == true) {
            binding.showGuide = false
        } else {
            viewModel.clickChange(this@MainActivity, nextFun = {
                finish()
            })
        }
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
        CloakUtils.putPointYep("guideview", this@MainActivity)
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
            CloakUtils.putPointYep("frontpageset", this@MainActivity)
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
            if (binding.showLoading == true) {
                return@setOnClickListener
            }
            jumpToServerList()
        }
    }

    private fun jumpToServerList() {
        if (ServiceData.deliverServerTransitions(this)) {
            jumpToServerListFun(false)
            binding.showLoading = false
        } else {
            lifecycleScope.launch {
                binding.showLoading = true
                ServiceData.deliverServerTransitions(this@MainActivity)
                delay(2000)
                binding.showLoading = false
                jumpToServerListFun(true)
            }
        }
    }

    private fun jumpToServerListFun(isEmpty:Boolean){
        viewModel.clickChange(this@MainActivity, nextFun = {
            viewModel.jumpToServerList(this,isEmpty)
        })
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
                    CloakUtils.putPointYep("switchprotocol", this@MainActivity)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }.create()
        dialogVpn.setCancelable(false)
        dialogVpn.show()
        CloakUtils.putPointYep("protocol", this@MainActivity)
    }

    fun toClickConnect(v: View) {
        if (binding.showLoading == true) {
            return
        }
        if (ServiceData.deliverServerTransitions(this)) {
            clickConnectFun(v)
            binding.showLoading = false
        } else {
            lifecycleScope.launch {
                binding.showLoading = true
                ServiceData.deliverServerTransitions(this@MainActivity)
                delay(2000)
                binding.showLoading = false
            }
        }
    }


    private fun clickConnectFun(v: View) {
        viewModel.connectClickFun(v, this)
        if (binding.vpnState == 1) {
            return
        }
        lifecycleScope.launch {
            toConnectVpn()
        }
        if (binding.showGuide == true) {
            CloakUtils.putPointYep("guideclick", this)
            binding.showGuide = false
        }
    }

    private fun toConnectVpn() {
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
        if (!DataUtils.rl_data) {
            CloakUtils.putPointYep("notflow", this)
        }
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
            YepOkHttpUtils().getCurrentIp(this@MainActivity)
        }
        if (it) {
            Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show()
        } else {
            if (!DataUtils.isPermissionsYep) {
                CloakUtils.putPointYep("permission", this)
                DataUtils.isPermissionsYep = true
            }
            viewModel.startTheJudgment(this)
        }
    }


    private fun requestPermissionForResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            viewModel.startTheJudgment(this)
            if (!DataUtils.isPermissionsYep) {
                CloakUtils.putPointYep("获得VPN权限数量", this)
                DataUtils.isPermissionsYep = true
            }
        } else {
            Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show()
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
            if (DataUtils.agreement_type != 2) {
                return
            }
            Log.e(TAG, "newStatus: ${state}", )
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
                    viewModel.getConnectTime(this@MainActivity)

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
        viewModel.connectingStopFun(this)
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
            Log.e(
                TAG,
                "onActivityResult: =${com.yep.online.link.prox.connection.base.App.serviceState}"
            )
            when (com.yep.online.link.prox.connection.base.App.serviceState) {
                0 -> {
                    viewModel.updateSkServer(false)
                }

                1 -> {
                    viewModel.updateSkServer(true)
                }
            }
            com.yep.online.link.prox.connection.base.App.serviceState = -1
        }
    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        App.vpnState = state.canStop
        viewModel.getConnectTime(this)
        Log.e(
            TAG,
            "stateChanged: App.vpnState=${App.vpnState}"
        )
        viewModel.changeState(state, this)
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        val state = BaseService.State.values()[service.state]
        setSsVpnState(state.canStop)
    }

    private fun setSsVpnState(canStop: Boolean) {
        if (DataUtils.agreement_type != 2) {
            App.vpnState = canStop
            Log.e(
                TAG,
                "setSsVpnState: App.vpnState=${App.vpnState}"
            )
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